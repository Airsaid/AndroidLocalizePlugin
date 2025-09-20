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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airsaid.localization.constant.Constants
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.flagEmoji
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.utils.LanguageUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

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
        panel.preferredSize = java.awt.Dimension(680, 560)
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
                            properties().setValue(Constants.KEY_IS_OVERWRITE_EXISTING_STRING, checked)
                        },
                        onOpenTranslatedFileChanged = { checked ->
                            openTranslatedFileState.value = checked
                            properties().setValue(Constants.KEY_IS_OPEN_TRANSLATED_FILE, checked)
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
                                properties().setValue(Constants.KEY_IS_SELECT_ALL, allSelected)
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
        onClickListener?.onClickListener(selectedLanguages.toList())
        super.doOKAction()
    }

    override fun getDimensionServiceKey(): String? {
        val key = translator.key
        return "#com.airsaid.localization.ui.SelectLanguagesDialog#$key"
    }

    private fun initState() {
        val properties = properties()
        translator = translatorService.getSelectedTranslator() ?: error("Translator is not available")
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
        properties().setValue(Constants.KEY_IS_SELECT_ALL, checked)
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
        Text(
            text = "${translator.name} Translator",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

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
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
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
) {
    val filteredLanguages = remember(filterText, allLanguages) {
        if (filterText.isBlank()) allLanguages
        else allLanguages.filter {
            it.englishName.contains(filterText, ignoreCase = true) ||
                it.code.contains(filterText, ignoreCase = true)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp),
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
            OutlinedTextField(
                value = filterText,
                onValueChange = onFilterChange,
                label = { Text("Filter languages") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = selectAll,
                    onClick = { onSelectAllChanged(!selectAll) },
                    label = { Text("Select all") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                )
                FilterChip(
                    selected = overwriteExisting,
                    onClick = { onOverwriteChanged(!overwriteExisting) },
                    label = { Text("Overwrite existing") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                )
                FilterChip(
                    selected = openTranslatedFile,
                    onClick = { onOpenTranslatedFileChanged(!openTranslatedFile) },
                    label = { Text("Open translated file") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                )
            }

            Text(
                text = "Languages (${filteredLanguages.size}/${allLanguages.size})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        val columns = if (languages.size < 10) GridCells.Fixed(2) else GridCells.Adaptive(180.dp)
        LazyVerticalGrid(
            columns = columns,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(languages, key = { it.id }) { language ->
                val isSelected = language in selectedLanguages
                FilterChip(
                    selected = isSelected,
                    onClick = { onLanguageToggled(language, !isSelected) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    label = {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val flag = language.flagEmoji
                                if (flag != null) {
                                    Text(text = flag, style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(text = language.englishName, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                text = language.code.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )
            }
        }
    }
}
