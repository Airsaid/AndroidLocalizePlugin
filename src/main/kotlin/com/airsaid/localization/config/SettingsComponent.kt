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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.ui.IdeTheme
import com.airsaid.localization.ui.SupportLanguagesDialog
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import javax.swing.JComponent
import java.awt.Dimension

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
    private val appIdState = mutableStateOf("")
    private val appKeyState = mutableStateOf("")
    private val enableCacheState = mutableStateOf(true)
    private val maxCacheSizeState = mutableStateOf("500")
    private val translationIntervalState = mutableStateOf("2")

    init {
        composePanel.preferredSize = Dimension(680, 560)
        composePanel.setContent {
            IdeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SettingsContent(
                        translators = translatorsState,
                        selectedTranslator = selectedTranslatorState.value,
                        appIdState = appIdState,
                        appKeyState = appKeyState,
                        enableCacheState = enableCacheState,
                        maxCacheSizeState = maxCacheSizeState,
                        translationIntervalState = translationIntervalState,
                        onTranslatorSelected = { translator ->
                            applySelectedTranslator(translator)
                        },
                        onShowSupportedLanguages = { translator -> SupportLanguagesDialog(translator).show() },
                        onNavigateToApplyPage = { url -> BrowserUtil.browse(url) }
                    )
                }
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

    fun setAppId(appId: String) {
        appIdState.value = appId
    }

    fun setAppKey(appKey: String) {
        appKeyState.value = appKey
    }

    val appId: String
        get() = appIdState.value

    val appKey: String
        get() = appKeyState.value

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
        appIdState.value = ""
        appKeyState.value = ""

        credentialsLoader.load(translator) { appId, appKey ->
            if (selectedTranslatorState.value?.key == translator.key) {
                appIdState.value = appId
                appKeyState.value = appKey
            }
        }
    }
}

@Composable
private fun SettingsContent(
    translators: SnapshotStateList<AbstractTranslator>,
    selectedTranslator: AbstractTranslator?,
    appIdState: androidx.compose.runtime.MutableState<String>,
    appKeyState: androidx.compose.runtime.MutableState<String>,
    enableCacheState: androidx.compose.runtime.MutableState<Boolean>,
    maxCacheSizeState: androidx.compose.runtime.MutableState<String>,
    translationIntervalState: androidx.compose.runtime.MutableState<String>,
    onTranslatorSelected: (AbstractTranslator) -> Unit,
    onShowSupportedLanguages: (AbstractTranslator) -> Unit,
    onNavigateToApplyPage: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Translator",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            var dropdownExpanded by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(onClick = { dropdownExpanded = true }) {
                    Text(selectedTranslator?.name ?: "Select translator")
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.widthIn(min = 240.dp)
                ) {
                    translators.forEach { translator ->
                        DropdownMenuItem(
                            text = { Text(translator.name) },
                            onClick = {
                                dropdownExpanded = false
                                onTranslatorSelected(translator)
                            }
                        )
                    }
                }
            }

            val needsAppId = selectedTranslator?.isNeedAppId == true
            val needsAppKey = selectedTranslator?.isNeedAppKey == true

            if (needsAppId) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = appIdState.value,
                    onValueChange = { appIdState.value = it },
                    singleLine = true,
                    label = { Text(selectedTranslator.appIdDisplay) }
                )
            }

            if (needsAppKey) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = appKeyState.value,
                    onValueChange = { appKeyState.value = it },
                    singleLine = true,
                    label = { Text(selectedTranslator.appKeyDisplay) }
                )
            }

            if (!selectedTranslator?.applyAppIdUrl.isNullOrEmpty()) {
                TextButton(onClick = { onNavigateToApplyPage(selectedTranslator.applyAppIdUrl!!) }) {
                    Text("Apply for ${selectedTranslator.name} API credentials")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable translation cache")
                Switch(
                    checked = enableCacheState.value,
                    onCheckedChange = { enableCacheState.value = it },
                    colors = SwitchDefaults.colors()
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = maxCacheSizeState.value,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    maxCacheSizeState.value = digits.ifEmpty { "0" }
                },
                label = { Text("Max cache size") },
                enabled = enableCacheState.value,
                singleLine = true
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = translationIntervalState.value,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    translationIntervalState.value = digits.ifEmpty { "0" }
                },
                label = { Text("Translation interval (seconds)") },
                singleLine = true
            )

            selectedTranslator?.let {
                OutlinedButton(onClick = { onShowSupportedLanguages(it) }) {
                    Text("Supported languages")
                }
            }
        }
    }
}
