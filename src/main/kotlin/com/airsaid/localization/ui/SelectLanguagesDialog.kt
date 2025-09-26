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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.airsaid.localization.ui.components.IdeCheckbox
import com.airsaid.localization.ui.components.SwingIcon
import com.airsaid.localization.utils.LanguageUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import java.awt.Dimension
import java.awt.Toolkit
import kotlin.math.roundToInt

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

  private val translatorService = TranslatorService.getInstance()
  private val translator = translatorService.getSelectedTranslator()
  private val supportedLanguages = translator.supportedLanguages.sortedBy { it.code }
  private val defaultFavoriteCodes = Languages.defaultFavoriteCodes()

  private val favoriteLanguages = mutableStateListOf<Lang>()
  private val selectedLanguages = mutableStateListOf<Lang>()
  private val overwriteExistingState = mutableStateOf(false)
  private val openTranslatedFileState = mutableStateOf(false)

  private var stateInitialized by mutableStateOf(false)

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

  @Composable
  override fun Content() {
    LaunchedEffect(Unit) {
      if (!stateInitialized) {
        loadState()
      }
    }

    if (!stateInitialized) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
      return
    }

    val languages by remember {
      derivedStateOf { translator.supportedLanguages.filterNot { favoriteLanguages.contains(it) } }
    }

    Surface(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      color = MaterialTheme.colorScheme.background,
    ) {
      SelectLanguagesContent(
        translator = translator,
        languages = languages,
        favoriteLanguages = favoriteLanguages,
        selectedLanguages = selectedLanguages,
        overwriteExistingChecked = overwriteExistingState.value,
        openTranslatedFileChecked = openTranslatedFileState.value,
        onSelectAllChanged = { selectAll(languages, it) },
        onFavoriteSelectAllChanged = { selectAll(favoriteLanguages, it) },
        onOverwriteChanged = { checked -> overwriteExistingState.value = checked },
        onOpenTranslatedFileChanged = { checked -> openTranslatedFileState.value = checked },
        onLanguageToggled = { lang, checked -> selectLanguage(lang, checked) },
        onFavoriteToggle = { lang, isFavorite -> setFavoriteLanguage(lang, isFavorite) },
        onOpenSettings = { openPluginSettings() },
      )
    }

    OnClickOK {
      LanguageUtil.saveSelectedLanguages(project, selectedLanguages)
      properties().setValue(Constants.KEY_IS_OVERWRITE_EXISTING_STRING, overwriteExistingState.value)
      properties().setValue(Constants.KEY_IS_OPEN_TRANSLATED_FILE, openTranslatedFileState.value)
      onClickListener?.onClickListener(selectedLanguages.toList())
    }

    okAction.isEnabled = selectedLanguages.isNotEmpty()
  }

  override fun preferredSize(): Dimension {
    val (preferred, minimum) = calculateDialogSize()
    window?.minimumSize = minimum
    return preferred
  }

  override fun getDimensionServiceKey(): String {
    val key = translatorService.getSelectedTranslator().key
    return "#com.airsaid.localization.ui.SelectLanguagesDialog#$key"
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

  private fun properties(): PropertiesComponent {
    return PropertiesComponent.getInstance(project)
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
  onSelectAllChanged: (Boolean) -> Unit,
  onFavoriteSelectAllChanged: (Boolean) -> Unit,
  onOverwriteChanged: (Boolean) -> Unit,
  onOpenTranslatedFileChanged: (Boolean) -> Unit,
  onLanguageToggled: (Lang, Boolean) -> Unit,
  onFavoriteToggle: (Lang, Boolean) -> Unit,
  onOpenSettings: () -> Unit,
) {
  var filterText by rememberSaveable { mutableStateOf("") }

  val favoriteSelectAllChecked by remember(selectedLanguages) {
    derivedStateOf { favoriteLanguages.isNotEmpty() && favoriteLanguages.all { selectedLanguages.contains(it) } }
  }
  val languagesSelectAllChecked by remember(selectedLanguages) {
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
      onSelectAllChanged = onSelectAllChanged,
      onFavoriteSelectAllChanged = onFavoriteSelectAllChanged,
      onOverwriteChanged = onOverwriteChanged,
      onOpenTranslatedFileChanged = onOpenTranslatedFileChanged,
      onLanguageToggled = onLanguageToggled,
      onFavoriteToggle = onFavoriteToggle,
      modifier = Modifier.weight(1f, fill = true),
    )

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
  onSelectAllChanged: (Boolean) -> Unit,
  onFavoriteSelectAllChanged: (Boolean) -> Unit,
  onOverwriteChanged: (Boolean) -> Unit,
  onOpenTranslatedFileChanged: (Boolean) -> Unit,
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

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = 260.dp),
    tonalElevation = 0.dp,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
    color = MaterialTheme.colorScheme.surface,
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
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
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
  isFavorite: Boolean,
  onToggle: (Boolean) -> Unit,
  onFavoriteToggle: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundColor =
    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
  val borderColor =
    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

  Row(
    modifier = modifier
      .height(64.dp)
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
    Text(
      text = language.flag,
      style = MaterialTheme.typography.headlineMedium,
    )
    Column(
      modifier = Modifier.weight(1f, fill = true),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = language.name,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = "${language.englishName} (${language.code})",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
    }
    IconToggleButton(
      checked = isFavorite,
      onCheckedChange = onFavoriteToggle,
      modifier = Modifier.size(32.dp),
    ) {
      Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
        tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
          alpha = 0.45f
        ),
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
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    TooltipArea(
      tooltip = {
        Surface(
          shape = RoundedCornerShape(6.dp),
          shadowElevation = 4.dp,
          color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
          Text(
            text = "Open plugin settings",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    ) {
      IconButton(onClick = onOpenSettings) {
        Icon(
          imageVector = Icons.Filled.Settings,
          contentDescription = "Open plugin settings",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
