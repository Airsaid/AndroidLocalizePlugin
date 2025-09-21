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

package com.airsaid.localization.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airsaid.localization.constant.Constants
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.flagEmoji
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.utils.LanguageUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.JComponent
import kotlin.math.roundToInt

/**
 * Compose-driven dialog used to pick the languages that should be generated.
 */
class SelectLanguagesDialog(private val project: Project?) : DialogWrapper(project, false) {

    interface OnClickListener {
        fun onClickListener(selectedLanguage: List<Lang>)
    }

    private val translatorService = TranslatorService.getInstance()
    private val selectedLanguages = mutableStateListOf<Lang>()
    private val selectAllState = mutableStateOf(false)
    private val overwriteExistingState = mutableStateOf(false)
    private val openTranslatedFileState = mutableStateOf(false)

    private var onClickListener: OnClickListener? = null

    private lateinit var translator: AbstractTranslator
    private lateinit var supportedLanguages: List<Lang>

    init {
        initState()
        title = "Select Translated Languages"
        init()
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    override fun createCenterPanel(): JComponent {
        val panel = ComposePanel()
        val (preferredSize, minimumSize) = calculateDialogSize()
        panel.preferredSize = preferredSize
        panel.minimumSize = minimumSize
        panel.setContent {
            IdeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    SelectLanguagesContent(
                        translator = translator,
                        supportedLanguages = supportedLanguages,
                        selectedLanguages = selectedLanguages,
                        selectAllStateChecked = selectAllState.value,
                        overwriteExistingChecked = overwriteExistingState.value,
                        openTranslatedFileChecked = openTranslatedFileState.value,
                        onSelectAllChanged = { handleSelectAll(it) },
                        onOverwriteChanged = { checked ->
                            overwriteExistingState.value = checked
                        },
                        onOpenTranslatedFileChanged = { checked ->
                            openTranslatedFileState.value = checked
                        },
                        onLanguageToggled = { lang, checked ->
                            if (checked) {
                                if (!selectedLanguages.contains(lang)) {
                                    selectedLanguages.add(lang)
                                }
                            } else {
                                selectedLanguages.remove(lang)
                            }

                            val allSelected = selectedLanguages.size == supportedLanguages.size && supportedLanguages.isNotEmpty()
                            if (selectAllState.value != allSelected) {
                                selectAllState.value = allSelected
                            }

                            okAction.isEnabled = selectedLanguages.isNotEmpty()
                        },
                    )
                }
            }
        }
        return panel
    }

    override fun doOKAction() {
        project?.let { LanguageUtil.saveSelectedLanguage(it, selectedLanguages) }
        properties().setValue(Constants.KEY_IS_SELECT_ALL, selectAllState.value)
        properties().setValue(Constants.KEY_IS_OVERWRITE_EXISTING_STRING, overwriteExistingState.value)
        properties().setValue(Constants.KEY_IS_OPEN_TRANSLATED_FILE, openTranslatedFileState.value)
        onClickListener?.onClickListener(selectedLanguages.toList())
        super.doOKAction()
    }

    override fun getDimensionServiceKey(): String? {
        val key = translator.key
        return "#com.airsaid.localization.ui.SelectLanguagesDialog#$key"
    }

    private fun initState() {
        val properties = properties()
        translator = translatorService.getSelectedTranslator()
        supportedLanguages = translator.supportedLanguages.sortedBy { it.englishName }

        val savedLanguageIds = LanguageUtil.getSelectedLanguageIds(project)
        selectedLanguages.clear()
        if (!savedLanguageIds.isNullOrEmpty()) {
            selectedLanguages.addAll(supportedLanguages.filter { savedLanguageIds.contains(it.id.toString()) })
        }

        selectAllState.value = properties.getBoolean(Constants.KEY_IS_SELECT_ALL)
        if (selectAllState.value) {
            selectedLanguages.clear()
            selectedLanguages.addAll(supportedLanguages)
        }

        overwriteExistingState.value = properties.getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING)
        openTranslatedFileState.value = properties.getBoolean(Constants.KEY_IS_OPEN_TRANSLATED_FILE)

        okAction.isEnabled = selectedLanguages.isNotEmpty()
    }

    private fun handleSelectAll(checked: Boolean) {
        selectAllState.value = checked
        if (checked) {
            selectedLanguages.clear()
            selectedLanguages.addAll(supportedLanguages)
        } else {
            selectedLanguages.clear()
        }
        okAction.isEnabled = selectedLanguages.isNotEmpty()
    }

    private fun properties(): PropertiesComponent {
        return if (project != null) PropertiesComponent.getInstance(project) else PropertiesComponent.getInstance()
    }

    private fun calculateDialogSize(): Pair<Dimension, Dimension> {
        val screen = Toolkit.getDefaultToolkit().screenSize
        val aspectRatio = 1.45
        val maxWidth = (screen.width * 0.85).roundToInt()
        val minWidth = 900
        var width = (screen.width * 0.62).roundToInt().coerceIn(minWidth, maxWidth)

        val maxHeight = (screen.height * 0.8).roundToInt()
        val minHeight = 620
        var height = (width / aspectRatio).roundToInt().coerceAtLeast(minHeight)

        if (height > maxHeight) {
            height = maxHeight
            width = (height * aspectRatio).roundToInt().coerceAtMost(maxWidth)
        }

        val preferred = Dimension(width, height)
        val minimum = Dimension(minWidth, minHeight)
        return preferred to minimum
    }
}

@Composable
private fun SelectLanguagesContent(
    translator: AbstractTranslator,
    supportedLanguages: List<Lang>,
    selectedLanguages: SnapshotStateList<Lang>,
    selectAllStateChecked: Boolean,
    overwriteExistingChecked: Boolean,
    openTranslatedFileChecked: Boolean,
    onSelectAllChanged: (Boolean) -> Unit,
    onOverwriteChanged: (Boolean) -> Unit,
    onOpenTranslatedFileChanged: (Boolean) -> Unit,
    onLanguageToggled: (Lang, Boolean) -> Unit,
) {
    var filterText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        LanguagesCard(
            filterText = filterText,
            onFilterChange = { filterText = it },
            allLanguages = supportedLanguages,
            selectedLanguages = selectedLanguages,
            selectAll = selectAllStateChecked,
            overwriteExisting = overwriteExistingChecked,
            openTranslatedFile = openTranslatedFileChecked,
            onSelectAllChanged = onSelectAllChanged,
            onOverwriteChanged = onOverwriteChanged,
            onOpenTranslatedFileChanged = onOpenTranslatedFileChanged,
            onLanguageToggled = onLanguageToggled,
            modifier = Modifier.weight(1f, fill = true),
        )

        TranslatorFooter(translator = translator)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguagesCard(
    filterText: String,
    onFilterChange: (String) -> Unit,
    allLanguages: List<Lang>,
    selectedLanguages: SnapshotStateList<Lang>,
    selectAll: Boolean,
    overwriteExisting: Boolean,
    openTranslatedFile: Boolean,
    onSelectAllChanged: (Boolean) -> Unit,
    onOverwriteChanged: (Boolean) -> Unit,
    onOpenTranslatedFileChanged: (Boolean) -> Unit,
    onLanguageToggled: (Lang, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredLanguages = remember(filterText, allLanguages) {
        if (filterText.isBlank()) allLanguages
        else allLanguages.filter {
            it.englishName.contains(filterText, ignoreCase = true) ||
                it.code.contains(filterText, ignoreCase = true)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 260.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OptionsSection(
                overwriteExisting = overwriteExisting,
                onOverwriteChanged = onOverwriteChanged,
                openTranslatedFile = openTranslatedFile,
                onOpenTranslatedFileChanged = onOpenTranslatedFileChanged,
            )

            OutlinedTextField(
                value = filterText,
                onValueChange = onFilterChange,
                label = { Text("Filter languages") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            LanguagesHeader(
                total = allLanguages.size,
                selected = selectedLanguages.size,
                selectAll = selectAll,
                onSelectAllChanged = onSelectAllChanged,
            )

            LanguagesGrid(
                languages = filteredLanguages,
                selectedLanguages = selectedLanguages,
                onLanguageToggled = onLanguageToggled,
            )
        }
    }
}

@Composable
private fun LanguagesHeader(
    total: Int,
    selected: Int,
    selectAll: Boolean,
    onSelectAllChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Languages ($selected/$total)",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        OptionItem(
            text = "Select all",
            tooltip = "Select every supported language.",
            checked = selectAll,
            onCheckedChange = onSelectAllChanged,
        )
    }
}

@Composable
private fun LanguagesGrid(
    languages: List<Lang>,
    selectedLanguages: SnapshotStateList<Lang>,
    onLanguageToggled: (Lang, Boolean) -> Unit,
) {
    if (languages.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = "No languages match your filter", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(languages, key = { it.id }) { language ->
                LanguageOption(
                    language = language,
                    isSelected = language in selectedLanguages,
                    onToggle = { checked -> onLanguageToggled(language, checked) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptionsSection(
    overwriteExisting: Boolean,
    onOverwriteChanged: (Boolean) -> Unit,
    openTranslatedFile: Boolean,
    onOpenTranslatedFileChanged: (Boolean) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OptionItem(
            text = "Overwrite existing",
            tooltip = "Replace existing strings when a translation already exists.",
            checked = overwriteExisting,
            onCheckedChange = onOverwriteChanged,
        )
        OptionItem(
            text = "Open translated file",
            tooltip = "Open the generated translation file after the task finishes.",
            checked = openTranslatedFile,
            onCheckedChange = onOpenTranslatedFileChanged,
        )
    }
}

@Composable
private fun OptionItem(
    text: String,
    tooltip: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.toggleable(
            value = checked,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        )
    ) {
        IdeCheckbox(checked = checked)
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
        TooltipIcon(text = tooltip)
    }
}

@Composable
private fun IdeCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(3.dp)
    val colors = MaterialTheme.colorScheme
    val backgroundColor = when {
        !enabled -> colors.surface
        checked -> colors.primary
        else -> colors.surface
    }
    val borderColor = when {
        !enabled -> colors.outline.copy(alpha = 0.3f)
        checked -> colors.primary
        else -> colors.outline.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .size(16.dp)
            .border(1.dp, borderColor, shape)
            .background(backgroundColor, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.onPrimary,
                modifier = Modifier.size(10.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TooltipIcon(text: String) {
    TooltipArea(
        tooltip = {
            Surface(
                shape = RoundedCornerShape(6.dp),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        delayMillis = 300,
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LanguageOption(
    language: Lang,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val flag = language.flagEmoji
    val displayName = remember(language) { "${language.englishName} (${language.code.uppercase()})" }
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = 64.dp)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .toggleable(
                value = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Checkbox,
                onValueChange = onToggle,
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IdeCheckbox(checked = isSelected)
        if (flag != null) {
            Text(
                text = flag,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
            )
        }
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TranslatorFooter(translator: AbstractTranslator) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TranslatorIcon(icon = translator.icon)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${translator.name} Translator",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TranslatorIcon(icon: javax.swing.Icon?, modifier: Modifier = Modifier) {
    if (icon == null) return
    val imageBitmap = remember(icon) { icon.toImageBitmap() }
    Image(
        painter = remember(imageBitmap) { BitmapPainter(imageBitmap) },
        contentDescription = null,
        modifier = modifier.size(20.dp),
    )
}

private fun javax.swing.Icon.toImageBitmap(): ImageBitmap {
    val image = java.awt.image.BufferedImage(iconWidth, iconHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    graphics.background = java.awt.Color(0, 0, 0, 0)
    paintIcon(null, graphics, 0, 0)
    graphics.dispose()
    return image.toComposeImageBitmap()
}
