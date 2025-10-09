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

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airsaid.localization.config.SettingsConfigurable
import com.airsaid.localization.constant.Constants
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.lang.Lang
import com.airsaid.localization.translate.lang.Languages
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.ui.components.SwingIcon
import com.airsaid.localization.ui.components.TooltipIcon
import com.airsaid.localization.utils.LanguageUtil
import com.intellij.icons.AllIcons
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*

/**
 * Compose-driven dialog used to pick the languages that should be generated.
 *
 * @author airsaid
 */
class SelectLanguagesDialog(private val project: Project) : ComposeDialog(project, false) {

  /**
   * Callback for when the OK button is clicked.
   */
  interface OnClickListener {
    /**
     * Called when the OK button is clicked.
     *
     * @param selectedLanguage The list of selected languages.
     */
    fun onClickListener(selectedLanguage: List<Lang>)
  }

  override val defaultPreferredSize
    get() = 900 to 620

  private val translatorService = TranslatorService.getInstance()
  private val translator = translatorService.getSelectedTranslator()
  private val supportedLanguages = translator.supportedLanguages.sortedBy { it.code }
  private val defaultFavoriteCodes = Languages.defaultFavoriteCodes()

  private val favoriteLanguages = mutableStateListOf<Lang>()
  private val selectedLanguages = mutableStateListOf<Lang>()
  private val overwriteExistingState = mutableStateOf(false)
  private val openTranslatedFileState = mutableStateOf(false)
  private val autoSelectExistingState = mutableStateOf(false)

  private var stateInitialized by mutableStateOf(false)
  private var resourceDir: VirtualFile? = null

  private var onClickListener: OnClickListener? = null

  init {
    title = "Select Translated Languages"
  }

  /**
   * Sets the listener to be invoked when the OK button is clicked.
   *
   * @param listener The listener to be invoked.
   */
  fun setOnClickListener(listener: OnClickListener) {
    onClickListener = listener
  }

  /**
   * Sets the resource directory to enable auto-selecting existing languages.
   *
   * @param resourceDir The resource directory containing values folders.
   */
  fun setResourceDir(resourceDir: VirtualFile) {
    this.resourceDir = resourceDir
  }

  override fun getDimensionServiceKey(): String {
    val key = translatorService.getSelectedTranslator().key
    return "#com.airsaid.localization.ui.SelectLanguagesDialog#$key"
  }

  @Composable
  override fun Content() {
    LaunchedEffect(Unit) {
      if (!stateInitialized) {
        loadState()
      }
    }

    // Monitor changes to auto-select setting
    LaunchedEffect(autoSelectExistingState.value) {
      if (stateInitialized && autoSelectExistingState.value) {
        selectExistingLanguages()
      }
    }

    if (!stateInitialized) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
      return
    }

    val languages by remember(favoriteLanguages) {
      derivedStateOf { translator.supportedLanguages.filterNot { favoriteLanguages.contains(it) } }
    }

    SelectLanguagesContent(
      translator = translator,
      languages = languages,
      favoriteLanguages = favoriteLanguages,
      selectedLanguages = selectedLanguages,
      overwriteExistingChecked = overwriteExistingState.value,
      openTranslatedFileChecked = openTranslatedFileState.value,
      autoSelectExistingChecked = autoSelectExistingState.value,
      hasResourceDir = resourceDir != null,
      onSelectAllChanged = { selectAll(languages, it) },
      onFavoriteSelectAllChanged = { selectAll(favoriteLanguages, it) },
      onOverwriteChanged = { checked -> overwriteExistingState.value = checked },
      onOpenTranslatedFileChanged = { checked -> openTranslatedFileState.value = checked },
      onAutoSelectExistingChanged = { checked -> autoSelectExistingState.value = checked },
      onLanguageToggled = { lang, checked -> selectLanguage(lang, checked) },
      onFavoriteToggle = { lang, isFavorite -> setFavoriteLanguage(lang, isFavorite) },
      onOpenSettings = { openPluginSettings() },
    )

    OnClickOK {
      LanguageUtil.saveSelectedLanguages(project, selectedLanguages)
      properties().setValue(Constants.KEY_IS_OVERWRITE_EXISTING_STRING, overwriteExistingState.value)
      properties().setValue(Constants.KEY_IS_OPEN_TRANSLATED_FILE, openTranslatedFileState.value)
      properties().setValue(Constants.KEY_IS_AUTO_SELECT_EXISTING, autoSelectExistingState.value)
      onClickListener?.onClickListener(selectedLanguages.toList())
    }

    okAction.isEnabled = selectedLanguages.isNotEmpty()
  }

  private fun loadState() {
    val properties = properties()

    favoriteLanguages.clear()
    val favoriteLanguageCodes = LanguageUtil.getFavoriteLanguageIds(project)
    if (favoriteLanguageCodes.isNotEmpty()) {
      favoriteLanguages.addAll(supportedLanguages.filter { favoriteLanguageCodes.contains(it.code) })
    }
    if (favoriteLanguages.isEmpty() && defaultFavoriteCodes.isNotEmpty()) {
      favoriteLanguages.addAll(supportedLanguages.filter { defaultFavoriteCodes.contains(it.code) })
    }

    selectedLanguages.clear()
    val selectedLanguageCodes = LanguageUtil.getSelectedLanguageIds(project)
    if (selectedLanguageCodes.isNotEmpty()) {
      selectedLanguages.addAll(supportedLanguages.filter { selectedLanguageCodes.contains(it.code) })
    }

    overwriteExistingState.value = properties.getBoolean(Constants.KEY_IS_OVERWRITE_EXISTING_STRING)
    openTranslatedFileState.value = properties.getBoolean(Constants.KEY_IS_OPEN_TRANSLATED_FILE)
    autoSelectExistingState.value = properties.getBoolean(Constants.KEY_IS_AUTO_SELECT_EXISTING)

    // If auto-select existing languages is enabled and resource directory is available, auto-select existing languages
    if (autoSelectExistingState.value) {
      selectExistingLanguages()
    }

    stateInitialized = true
  }

  private fun selectAll(languages: List<Lang>, checked: Boolean) {
    languages.forEach { lang ->
      selectLanguage(lang, checked)
    }
  }

  private fun selectLanguage(lang: Lang, checked: Boolean) {
    if (checked) {
      if (!selectedLanguages.contains(lang)) {
        selectedLanguages.add(lang)
      }
    } else {
      selectedLanguages.remove(lang)
    }
  }

  private fun setFavoriteLanguage(lang: Lang, isFavorite: Boolean) {
    if (isFavorite) {
      if (!favoriteLanguages.contains(lang)) {
        favoriteLanguages.add(lang)
      }
    } else {
      if (favoriteLanguages.contains(lang)) {
        favoriteLanguages.remove(lang)
      }
    }
    LanguageUtil.saveFavoriteLanguages(project, favoriteLanguages)
  }

  private fun selectExistingLanguages() {
    resourceDir?.let { resDir ->
      val existingLanguages = LanguageUtil.getExistingProjectLanguages(resDir, supportedLanguages)
      existingLanguages.forEach { lang ->
        selectLanguage(lang, true)
      }
    }
  }

  private fun properties(): PropertiesComponent {
    return PropertiesComponent.getInstance(project)
  }

  private fun openPluginSettings() {
    ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable::class.java)
  }
}

@Composable
private fun SelectLanguagesContent(
  translator: AbstractTranslator,
  languages: List<Lang>,
  favoriteLanguages: SnapshotStateList<Lang>,
  selectedLanguages: SnapshotStateList<Lang>,
  overwriteExistingChecked: Boolean,
  openTranslatedFileChecked: Boolean,
  autoSelectExistingChecked: Boolean,
  hasResourceDir: Boolean,
  onSelectAllChanged: (Boolean) -> Unit,
  onFavoriteSelectAllChanged: (Boolean) -> Unit,
  onOverwriteChanged: (Boolean) -> Unit,
  onOpenTranslatedFileChanged: (Boolean) -> Unit,
  onAutoSelectExistingChanged: (Boolean) -> Unit,
  onLanguageToggled: (Lang, Boolean) -> Unit,
  onFavoriteToggle: (Lang, Boolean) -> Unit,
  onOpenSettings: () -> Unit,
) {
  var filterText by rememberSaveable { mutableStateOf("") }

  val favoriteSelectAllChecked by remember(selectedLanguages) {
    derivedStateOf { favoriteLanguages.isNotEmpty() && favoriteLanguages.all { selectedLanguages.contains(it) } }
  }
  val languagesSelectAllChecked by remember(selectedLanguages, languages) {
    derivedStateOf { languages.isNotEmpty() && languages.all { selectedLanguages.contains(it) } }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    LanguagesCard(
      filterText = filterText,
      onFilterChange = { filterText = it },
      languages = languages,
      favoriteLanguages = favoriteLanguages,
      selectedLanguages = selectedLanguages,
      selectAll = languagesSelectAllChecked,
      favoriteSelectAll = favoriteSelectAllChecked,
      overwriteExisting = overwriteExistingChecked,
      openTranslatedFile = openTranslatedFileChecked,
      autoSelectExisting = autoSelectExistingChecked,
      hasResourceDir = hasResourceDir,
      onSelectAllChanged = onSelectAllChanged,
      onFavoriteSelectAllChanged = onFavoriteSelectAllChanged,
      onOverwriteChanged = onOverwriteChanged,
      onOpenTranslatedFileChanged = onOpenTranslatedFileChanged,
      onAutoSelectExistingChanged = onAutoSelectExistingChanged,
      onLanguageToggled = onLanguageToggled,
      onFavoriteToggle = onFavoriteToggle,
      modifier = Modifier.weight(1f, fill = true),
    )

    Spacer(modifier = Modifier.height(10.dp))

    TranslatorFooter(translator = translator, onOpenSettings = onOpenSettings)
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguagesCard(
  filterText: String,
  onFilterChange: (String) -> Unit,
  languages: List<Lang>,
  favoriteLanguages: SnapshotStateList<Lang>,
  selectedLanguages: SnapshotStateList<Lang>,
  selectAll: Boolean,
  favoriteSelectAll: Boolean,
  overwriteExisting: Boolean,
  openTranslatedFile: Boolean,
  autoSelectExisting: Boolean,
  hasResourceDir: Boolean,
  onSelectAllChanged: (Boolean) -> Unit,
  onFavoriteSelectAllChanged: (Boolean) -> Unit,
  onOverwriteChanged: (Boolean) -> Unit,
  onOpenTranslatedFileChanged: (Boolean) -> Unit,
  onAutoSelectExistingChanged: (Boolean) -> Unit,
  onLanguageToggled: (Lang, Boolean) -> Unit,
  onFavoriteToggle: (Lang, Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val filteredLanguages = remember(filterText, languages) {
    if (filterText.isBlank()) {
      languages
    } else {
      languages.filter {
        it.code.contains(filterText, ignoreCase = true) ||
            it.englishName.contains(filterText, ignoreCase = true) ||
            it.name.contains(filterText, ignoreCase = true)
      }
    }
  }

  val filteredFavoriteLanguages = if (filterText.isBlank()) {
    favoriteLanguages.toList()
  } else {
    favoriteLanguages.filter {
      it.code.contains(filterText, ignoreCase = true) ||
          it.englishName.contains(filterText, ignoreCase = true) ||
          it.name.contains(filterText, ignoreCase = true)
    }
  }

  val favoriteSelectedCount by remember {
    derivedStateOf { favoriteLanguages.count { selectedLanguages.contains(it) } }
  }
  val selectedLanguagesCount by remember {
    derivedStateOf { selectedLanguages.count { !favoriteLanguages.contains(it) } }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = 260.dp)
      .border(1.dp, JewelTheme.globalColors.borders.normal, RoundedCornerShape(4.dp))
      .background(JewelTheme.globalColors.panelBackground),
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OptionsSection(
        overwriteExisting = overwriteExisting,
        onOverwriteChanged = onOverwriteChanged,
        openTranslatedFile = openTranslatedFile,
        onOpenTranslatedFileChanged = onOpenTranslatedFileChanged,
        autoSelectExisting = autoSelectExisting,
        hasResourceDir = hasResourceDir,
        onAutoSelectExistingChanged = onAutoSelectExistingChanged,
      )

      Divider(orientation = Orientation.Horizontal, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp))

      BasicTextField(
        value = filterText,
        onValueChange = onFilterChange,
        modifier = Modifier
          .fillMaxWidth()
          .background(JewelTheme.globalColors.panelBackground, RoundedCornerShape(4.dp))
          .border(1.dp, JewelTheme.globalColors.borders.normal, RoundedCornerShape(4.dp))
          .padding(horizontal = 10.dp, vertical = 10.dp),
        textStyle = JewelTheme.defaultTextStyle.copy(color = JewelTheme.globalColors.text.normal),
        cursorBrush = SolidColor(JewelTheme.globalColors.text.normal),
        singleLine = true,
        decorationBox = { innerTextField ->
          if (filterText.isEmpty()) {
            Text("Filter languages", color = JewelTheme.globalColors.text.info)
          }
          innerTextField()
        }
      )

      FavoriteLanguagesSection(
        favoriteLanguages = favoriteLanguages,
        filteredFavoriteLanguages = filteredFavoriteLanguages,
        selectedLanguages = selectedLanguages,
        selectedCount = favoriteSelectedCount,
        selectAll = favoriteSelectAll,
        onSelectAllChanged = onFavoriteSelectAllChanged,
        onLanguageToggled = onLanguageToggled,
        onFavoriteToggle = onFavoriteToggle,
      )

      LanguagesHeader(
        title = "Languages",
        total = languages.size,
        selected = selectedLanguagesCount,
      ) {
        OptionItem(
          text = "Select all",
          tooltip = "Select every supported language.",
          checked = selectAll,
          onCheckedChange = onSelectAllChanged,
        )
      }

      LanguagesGrid(
        languages = filteredLanguages,
        selectedLanguages = selectedLanguages,
        favoriteLanguages = favoriteLanguages,
        onLanguageToggled = onLanguageToggled,
        onFavoriteToggle = onFavoriteToggle,
        emptyMessage = "No languages match your filter",
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}

@Composable
private fun FavoriteLanguagesSection(
  favoriteLanguages: SnapshotStateList<Lang>,
  filteredFavoriteLanguages: List<Lang>,
  selectedLanguages: SnapshotStateList<Lang>,
  selectedCount: Int,
  selectAll: Boolean,
  onSelectAllChanged: (Boolean) -> Unit,
  onLanguageToggled: (Lang, Boolean) -> Unit,
  onFavoriteToggle: (Lang, Boolean) -> Unit,
) {
  val hasFavorites = favoriteLanguages.isNotEmpty()
  val languagesToDisplay = if (hasFavorites) filteredFavoriteLanguages else emptyList()
  val emptyMessage = when {
    !hasFavorites -> "No favorite languages yet. \nClick the star beside a language below to add it."
    else -> "No favorite languages match your filter"
  }
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    LanguagesHeader(
      title = "Favorite Languages",
      total = favoriteLanguages.size,
      selected = selectedCount,
    ) {
      OptionItem(
        text = "Select all",
        tooltip = "Select every favorite language.",
        checked = selectAll,
        onCheckedChange = onSelectAllChanged,
      )
    }

    LanguagesGrid(
      languages = languagesToDisplay,
      selectedLanguages = selectedLanguages,
      favoriteLanguages = favoriteLanguages,
      onLanguageToggled = onLanguageToggled,
      onFavoriteToggle = onFavoriteToggle,
      emptyMessage = emptyMessage,
      modifier = Modifier.fillMaxWidth().heightIn(max = 142.dp),
    )
  }
}

@Composable
private fun LanguagesHeader(
  title: String,
  total: Int,
  selected: Int,
  modifier: Modifier = Modifier,
  trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = "$title ($selected/$total)",
      fontWeight = FontWeight.Medium,
      color = JewelTheme.globalColors.text.info,
    )
    if (trailingContent != null) {
      Spacer(modifier = Modifier.weight(1f))
      trailingContent()
    }
  }
}

@Composable
private fun LanguagesGrid(
  languages: List<Lang>,
  selectedLanguages: SnapshotStateList<Lang>,
  favoriteLanguages: SnapshotStateList<Lang>,
  onLanguageToggled: (Lang, Boolean) -> Unit,
  onFavoriteToggle: (Lang, Boolean) -> Unit,
  emptyMessage: String,
  modifier: Modifier = Modifier,
  emptyAlignment: Alignment = Alignment.Center,
) {
  if (languages.isEmpty()) {
    Box(modifier = modifier, contentAlignment = emptyAlignment) {
      Text(
        text = emptyMessage,
        color = JewelTheme.globalColors.text.info,
        textAlign = TextAlign.Center,
      )
    }
  } else {
    val languagesGridState = rememberLazyGridState()
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      LazyVerticalGrid(
        state = languagesGridState,
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.wrapContentHeight().weight(1f, fill = true)
      ) {
        items(languages, key = { it.code }) { language ->
          LanguageOption(
            language = language,
            isSelected = language in selectedLanguages,
            isFavorite = language in favoriteLanguages,
            onToggle = { checked -> onLanguageToggled(language, checked) },
            onFavoriteToggle = { checked -> onFavoriteToggle(language, checked) },
          )
        }
      }
      VerticalScrollbar(rememberScrollbarAdapter(languagesGridState))
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
  autoSelectExisting: Boolean,
  hasResourceDir: Boolean,
  onAutoSelectExistingChanged: (Boolean) -> Unit,
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
    if (hasResourceDir) {
      OptionItem(
        text = "Auto-select existing languages",
        tooltip = "Automatically select languages that already exist in your project when opening this dialog.",
        checked = autoSelectExisting,
        onCheckedChange = onAutoSelectExistingChanged,
      )
    }
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
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    modifier = modifier.toggleable(
      value = checked,
      interactionSource = remember { MutableInteractionSource() },
      indication = null,
      role = Role.Checkbox,
      onValueChange = onCheckedChange,
    )
  ) {
    Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    Text(text = text)
    Spacer(modifier = Modifier.width(2.dp))
    TooltipIcon(text = tooltip)
  }
}

@Composable
private fun LanguageOption(
  language: Lang,
  isSelected: Boolean,
  isFavorite: Boolean,
  onToggle: (Boolean) -> Unit,
  onFavoriteToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundColor = JewelTheme.globalColors.panelBackground
  val borderColor = if (isSelected) JewelTheme.globalColors.outlines.focused else JewelTheme.globalColors.borders.normal

  Row(
    modifier = modifier
      .height(64.dp)
      .border(1.dp, borderColor, RoundedCornerShape(8.dp))
      .background(backgroundColor, RoundedCornerShape(8.dp))
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
    Checkbox(checked = isSelected, onCheckedChange = onToggle)
    Text(
      text = language.flag,
      modifier = Modifier.padding(4.dp),
    )
    Column(
      modifier = Modifier.weight(1f, fill = true),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = language.name,
        color = JewelTheme.globalColors.text.normal,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = "${language.englishName} (${language.code})",
        color = JewelTheme.globalColors.text.info,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    }
    IconButton(
      onClick = { onFavoriteToggle(!isFavorite) },
      modifier = Modifier.size(32.dp),
    ) {
      SwingIcon(
        icon = if (isFavorite) AllIcons.Nodes.Favorite else AllIcons.Nodes.NotFavoriteOnHover,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TranslatorFooter(translator: AbstractTranslator, onOpenSettings: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    SwingIcon(icon = translator.icon)
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = "${translator.name} Translator",
      fontWeight = FontWeight.Medium,
      color = JewelTheme.globalColors.text.info,
    )
    Tooltip(tooltip = { Text("Open plugin settings") }) {
      IconButton(onClick = onOpenSettings) {
        SwingIcon(
          icon = AllIcons.General.Settings,
          modifier = Modifier.size(16.dp),
          tintColor = JewelTheme.globalColors.text.info,
        )
      }
    }
  }
}