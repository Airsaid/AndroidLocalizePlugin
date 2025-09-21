/*
 * Copyright 2021 Airsaid. https://github.com/airsaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.airsaid.localization.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement as LayoutArrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.TranslatorCredentialDescriptor
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.ui.IdeTheme
import com.airsaid.localization.ui.SupportLanguagesDialog
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.ui.UIUtil
import java.awt.Dimension
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.JComponent
import kotlin.math.max

/**
 * Compose implementation of the settings panel exposed through the IDE Settings.
 */
class SettingsComponent(
    private val credentialsLoader: TranslatorCredentialsLoader = TranslatorCredentialsLoader.default()
) {
    companion object {
        private val LOG = Logger.getInstance(SettingsComponent::class.java)
    }

    private val composePanel = ComposePanel()

    private val translatorsState = mutableStateListOf<AbstractTranslator>()
    private val selectedTranslatorState = mutableStateOf<AbstractTranslator?>(null)
    private val credentialDefinitionsState = mutableStateListOf<TranslatorCredentialDescriptor>()
    private val credentialValuesState = mutableStateMapOf<String, String>()
    private val enableCacheState = mutableStateOf(true)
    private val maxCacheSizeState = mutableStateOf("500")
    private val translationIntervalState = mutableStateOf("2")

    init {
        composePanel.preferredSize = Dimension(680, 560)
        composePanel.isOpaque = true
        composePanel.background = UIUtil.getPanelBackground()
        composePanel.setContent {
            IdeTheme {
                SettingsContent(
                    translators = translatorsState,
                    selectedTranslator = selectedTranslatorState.value,
                    defaultTranslatorKey = TranslatorService.getInstance().getDefaultTranslator()?.key,
                    credentialDefinitions = credentialDefinitionsState,
                    credentialValues = credentialValuesState,
                    enableCacheState = enableCacheState,
                    maxCacheSizeState = maxCacheSizeState,
                    translationIntervalState = translationIntervalState,
                    onTranslatorSelected = { translator ->
                        applySelectedTranslator(translator)
                    },
                    onCredentialValueChanged = { id, value ->
                        credentialValuesState[id] = value
                    },
                    onShowSupportedLanguages = { translator -> SupportLanguagesDialog(translator).show() },
                    onNavigateToApplyPage = { url -> BrowserUtil.browse(url) }
                )
            }
        }
    }

    val content: JComponent
        get() = composePanel

    val preferredFocusedComponent: JComponent
        get() = composePanel

    val selectedTranslator: AbstractTranslator
        get() = selectedTranslatorState.value ?: error("Translator not selected")

    fun setTranslators(translators: Map<String, AbstractTranslator>) {
        LOG.info("setTranslators: ${translators.keys}")
        translatorsState.clear()
        translatorsState.addAll(translators.values)
        if (selectedTranslatorState.value == null && translatorsState.isNotEmpty()) {
            applySelectedTranslator(translatorsState.first())
        }
    }

    fun setSelectedTranslator(selected: AbstractTranslator) {
        applySelectedTranslator(selected)
    }

    fun setCredentialValues(values: Map<String, String>) {
        credentialValuesState.clear()
        credentialDefinitionsState.forEach { descriptor ->
            credentialValuesState[descriptor.id] = values[descriptor.id] ?: ""
        }
    }

    fun setCredentialValue(id: String, value: String) {
        credentialValuesState[id] = value
    }

    val credentialValues: Map<String, String>
        get() = credentialDefinitionsState.associate { descriptor ->
            descriptor.id to (credentialValuesState[descriptor.id] ?: "")
        }

    fun setEnableCache(isEnable: Boolean) {
        enableCacheState.value = isEnable
    }

    val isEnableCache: Boolean
        get() = enableCacheState.value

    fun setMaxCacheSize(maxCacheSize: Int) {
        maxCacheSizeState.value = maxCacheSize.toString()
    }

    val maxCacheSize: Int
        get() = maxCacheSizeState.value.toIntOrNull() ?: 0

    fun setTranslationInterval(intervalTime: Int) {
        translationIntervalState.value = intervalTime.toString()
    }

    val translationInterval: Int
        get() = translationIntervalState.value.toIntOrNull() ?: 0

    val isSelectedDefaultTranslator: Boolean
        get() = selectedTranslatorState.value?.let { isSelectedDefaultTranslator(it) } ?: false

    private fun isSelectedDefaultTranslator(selected: AbstractTranslator): Boolean {
        return selected == TranslatorService.getInstance().getDefaultTranslator()
    }

    private fun applySelectedTranslator(translator: AbstractTranslator) {
        selectedTranslatorState.value = translator
        credentialDefinitionsState.clear()
        credentialDefinitionsState.addAll(translator.credentialDefinitions)
        credentialValuesState.clear()
        translator.credentialDefinitions.forEach { descriptor ->
            credentialValuesState[descriptor.id] = ""
        }

        credentialsLoader.load(translator) { loaded ->
            if (selectedTranslatorState.value?.key == translator.key) {
                translator.credentialDefinitions.forEach { descriptor ->
                    credentialValuesState[descriptor.id] = loaded[descriptor.id] ?: ""
                }
            }
        }
    }
}

private val LabelColumnWidth = 176.dp
private val FieldMinWidth = 160.dp
private val CompactFieldHeight = 36.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    translators: SnapshotStateList<AbstractTranslator>,
    selectedTranslator: AbstractTranslator?,
    defaultTranslatorKey: String?,
    credentialDefinitions: SnapshotStateList<TranslatorCredentialDescriptor>,
    credentialValues: SnapshotStateMap<String, String>,
    enableCacheState: androidx.compose.runtime.MutableState<Boolean>,
    maxCacheSizeState: androidx.compose.runtime.MutableState<String>,
    translationIntervalState: androidx.compose.runtime.MutableState<String>,
    onTranslatorSelected: (AbstractTranslator) -> Unit,
    onCredentialValueChanged: (String, String) -> Unit = { _, _ -> },
    onShowSupportedLanguages: (AbstractTranslator) -> Unit,
    onNavigateToApplyPage: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = LayoutArrangement.spacedBy(20.dp)
    ) {
        SectionHeader(title = "Translator")

        SettingsFormRow(label = "Provider") {
            TranslatorDropdown(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = CompactFieldHeight),
                translators = translators,
                selectedTranslator = selectedTranslator,
                defaultTranslatorKey = defaultTranslatorKey,
                onTranslatorSelected = onTranslatorSelected
            )

            selectedTranslator?.let {
                TextButton(onClick = { onShowSupportedLanguages(it) }) {
                    Text("View supported languages")
                }
            }
        }

        selectedTranslator?.let { provider ->
            credentialDefinitions.forEach { descriptor ->
                SettingsFormRow(label = descriptor.label, helperText = descriptor.description) {
                    IdeTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(min = FieldMinWidth),
                        value = credentialValues[descriptor.id] ?: "",
                        onValueChange = { newValue ->
                            onCredentialValueChanged(descriptor.id, newValue.trimStart())
                        },
                        singleLine = true,
                        secureInput = descriptor.isSecret
                    )
                }
            }

            provider.credentialHelpUrl?.takeUnless { it.isBlank() }?.let { url ->
                SettingsFormRow(
                    label = "Need credentials?",
                    helperText = "Click to request keys from ${provider.name}."
                ) {
                    TextButton(onClick = { onNavigateToApplyPage(url) }) {
                        Text("Apply for API key")
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

        SectionHeader(title = "Caching")

        SettingsFormRow(label = "Use cache") {
            Switch(
                checked = enableCacheState.value,
                onCheckedChange = { enableCacheState.value = it }
            )
        }

        SettingsFormRow(
            label = "Max cache size",
            helperText = if (enableCacheState.value) {
                "Maximum cached translations before older ones are removed."
            } else {
                "Enable cache to edit the number of stored translations."
            }
        ) {
            IdeTextField(
                modifier = Modifier
                    .widthIn(min = FieldMinWidth, max = 220.dp),
                value = maxCacheSizeState.value,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    maxCacheSizeState.value = digits.ifEmpty { "0" }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                enabled = enableCacheState.value
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

        SectionHeader(title = "Requests")

        SettingsFormRow(
            label = "Interval (seconds)",
            helperText = "Delay between translation requests to avoid provider rate limits."
        ) {
            IdeTextField(
                modifier = Modifier
                    .widthIn(min = FieldMinWidth, max = 220.dp),
                value = translationIntervalState.value,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    translationIntervalState.value = digits.ifEmpty { "0" }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun SettingsFormRow(
    label: String,
    helperText: String? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .width(LabelColumnWidth)
                    .padding(end = 12.dp)
            )

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = LayoutArrangement.spacedBy(12.dp),
                content = content
            )
        }

        helperText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = LabelColumnWidth + 12.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    placeholder: (@Composable (() -> Unit))? = null,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    secureInput: Boolean = false,
    singleLine: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .heightIn(min = CompactFieldHeight)
            .defaultMinSize(minHeight = CompactFieldHeight),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
    ) { innerTextField ->
        val visualTransformation = if (secureInput) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            visualTransformation = visualTransformation,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = null,
            prefix = null,
            suffix = null,
            supportingText = null,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            enabled = enabled,
            isError = false,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = enabled,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TranslatorDropdown(
    modifier: Modifier = Modifier,
    translators: List<AbstractTranslator>,
    selectedTranslator: AbstractTranslator?,
    defaultTranslatorKey: String?,
    onTranslatorSelected: (AbstractTranslator) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPainter = selectedTranslator?.let { translatorIconPainter(it) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .heightIn(min = CompactFieldHeight),
            value = selectedTranslator?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Translator") },
            placeholder = { Text("Select translator") },
            singleLine = true,
            leadingIcon = {
                selectedPainter?.let {
                    Icon(
                        painter = it,
                        contentDescription = selectedTranslator?.name,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Unspecified
                    )
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            translators.forEach { translator ->
                val itemPainter = translatorIconPainter(translator)
                val isDefault = translator.key == defaultTranslatorKey

                DropdownMenuItem(
                    text = { Text(translator.name) },
                    leadingIcon = {
                        itemPainter?.let {
                            Icon(
                                painter = it,
                                contentDescription = translator.name,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Unspecified
                            )
                        }
                    },
                    trailingIcon = {
                        if (isDefault) {
                            DefaultBadge()
                        }
                    },
                    onClick = {
                        expanded = false
                        onTranslatorSelected(translator)
                    },
                    modifier = Modifier.widthIn(min = 240.dp)
                )
            }
        }
    }
}

@Composable
private fun DefaultBadge() {
    Text(
        text = "Default",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun translatorIconPainter(translator: AbstractTranslator): Painter? {
    val icon = translator.icon ?: return null
    return remember(icon) {
        val buffered = icon.toBufferedImageSafely()
        BitmapPainter(buffered.toComposeImageBitmap())
    }
}

private fun Icon.toBufferedImageSafely(): BufferedImage {
    val width = max(1, iconWidth)
    val height = max(1, iconHeight)
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
    paintIcon(null, graphics, 0, 0)
    graphics.dispose()
    return image
}
