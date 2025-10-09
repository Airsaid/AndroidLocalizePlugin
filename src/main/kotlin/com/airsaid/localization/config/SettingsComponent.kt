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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.ui.SupportedLanguagesDialog
import com.airsaid.localization.ui.components.IdeTextField
import com.airsaid.localization.ui.components.SwingIcon
import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.jewel.bridge.JewelComposePanel
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.*
import java.awt.Dimension
import javax.swing.JComponent
import androidx.compose.foundation.layout.Arrangement as LayoutArrangement

/**
 * Compose implementation of the settings panel exposed through the IDE Settings.
 *
 * @author airsaid
 */
class SettingsComponent {
  companion object {
    private val LOG = Logger.getInstance(SettingsComponent::class.java)
  }

  private val translatorsState = mutableStateListOf<AbstractTranslator>()
  private val selectedTranslatorState = mutableStateOf<AbstractTranslator?>(null)
  private val enableCacheState = mutableStateOf(true)
  private val maxCacheSizeState = mutableStateOf("500")
  private val translationIntervalState = mutableStateOf("50")

  private val composePanel = JewelComposePanel(config = {
    preferredSize = Dimension(680, 560)
    isOpaque = true
  }, content = {
    SettingsContent(
      translators = translatorsState,
      selectedTranslator = selectedTranslatorState.value,
      enableCacheState = enableCacheState,
      maxCacheSizeState = maxCacheSizeState,
      translationIntervalState = translationIntervalState,
      onTranslatorSelected = { translator -> applySelectedTranslator(translator) },
      onShowSupportedLanguages = { translator -> SupportedLanguagesDialog(translator).show() },
      onConfigureTranslator = { translator ->
        TranslatorConfigurationManager.showConfigurationDialog(translator)
      }
    )
  })

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
      applySelectedTranslator(TranslatorService.getInstance().getDefaultTranslator())
    }
  }

  fun setSelectedTranslator(selected: AbstractTranslator) {
    applySelectedTranslator(selected)
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
  }
}

private val LabelColumnWidth = 140.dp
private val FieldMinWidth = 160.dp
private val FormContentSpacing = 8.dp
private const val MAX_REQUEST_INTERVAL_MS = 60_000
private const val DONATION_URL =
  "https://github.com/Airsaid/AndroidLocalizePlugin/blob/main/README.md#support-and-donations"

@Composable
private fun SettingsContent(
  translators: SnapshotStateList<AbstractTranslator>,
  selectedTranslator: AbstractTranslator?,
  enableCacheState: MutableState<Boolean>,
  maxCacheSizeState: MutableState<String>,
  translationIntervalState: MutableState<String>,
  onTranslatorSelected: (AbstractTranslator) -> Unit,
  onShowSupportedLanguages: (AbstractTranslator) -> Unit,
  onConfigureTranslator: (AbstractTranslator) -> Unit,
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(JewelTheme.globalColors.panelBackground)
      .verticalScroll(scrollState)
      .padding(horizontal = 24.dp, vertical = 16.dp),
    verticalArrangement = LayoutArrangement.spacedBy(20.dp)
  ) {
    SectionHeader(title = "Translator")

    SettingsFormRow(label = "Provider") {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = LayoutArrangement.spacedBy(8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = LayoutArrangement.spacedBy(FormContentSpacing)
        ) {
          TranslatorDropdown(
            modifier = Modifier.width(220.dp).height(40.dp),
            translators = translators,
            selectedTranslator = selectedTranslator,
            onTranslatorSelected = onTranslatorSelected
          )

          selectedTranslator?.let { translator ->
            if (TranslatorConfigurationManager.hasConfiguration(translator)) {
              IconButton(onClick = { onConfigureTranslator(translator) }) {
                SwingIcon(
                  icon = AllIcons.General.Settings,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        selectedTranslator?.let { translator ->
          Link(
            text = "See supported languages",
            onClick = { onShowSupportedLanguages(translator) },
            modifier = Modifier.align(Alignment.Start)
          )
        }
      }
    }

    HorizontalDivider()

    SectionHeader(title = "Caching")

    SettingsFormRow(label = "Use cache") {
      Checkbox(
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
        modifier = Modifier.widthIn(min = FieldMinWidth, max = 220.dp),
        value = maxCacheSizeState.value,
        onValueChange = { newValue ->
          val digits = newValue.filter { it.isDigit() }
          maxCacheSizeState.value = digits.ifEmpty { "0" }
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        enabled = enableCacheState.value
      )
    }

    HorizontalDivider()

    SectionHeader(title = "Requests")

    SettingsFormRow(
      label = "Interval",
      helperText = "Delay between translation requests in milliseconds (max 60,000 ms ≈ 1 minute)."
    ) {
      IdeTextField(
        modifier = Modifier.widthIn(min = FieldMinWidth, max = 220.dp),
        value = translationIntervalState.value,
        onValueChange = { newValue ->
          val digits = newValue.filter { it.isDigit() }
          val clampedValue = digits.toLongOrNull()
            ?.coerceAtMost(MAX_REQUEST_INTERVAL_MS.toLong())
            ?.toInt()
          translationIntervalState.value = when {
            digits.isEmpty() -> "0"
            clampedValue == null -> MAX_REQUEST_INTERVAL_MS.toString()
            else -> clampedValue.toString()
          }
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        suffix = {
          Text(
            text = "ms",
            color = JewelTheme.globalColors.text.info,
            modifier = Modifier.padding(start = 4.dp)
          )
        }
      )
    }

    HorizontalDivider()

    SectionHeader(title = "Donation")
    DonationSection()
  }
}

@Composable
private fun SectionHeader(title: String) {
  Text(
    text = title,
    fontWeight = FontWeight.SemiBold,
    color = JewelTheme.globalColors.text.normal
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
        color = JewelTheme.globalColors.text.info,
        modifier = Modifier
          .width(LabelColumnWidth)
          .padding(end = 12.dp)
      )

      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = LayoutArrangement.spacedBy(FormContentSpacing),
        content = content
      )
    }

    helperText?.let {
      Text(
        text = it,
        color = JewelTheme.globalColors.text.info,
        modifier = Modifier.padding(start = LabelColumnWidth + FormContentSpacing, top = 4.dp)
      )
    }
  }
}

@Composable
private fun DonationSection() {
  Column {
    Text(
      text = "If this plugin has helped simplify your localization workflow,\nconsider supporting its ongoing development so it can continue to improve.",
      color = JewelTheme.globalColors.text.normal
    )

    Link(
      text = "Buy me a coffee  ☕",
      onClick = { BrowserUtil.browse(DONATION_URL) },
      modifier = Modifier.align(Alignment.Start)
    )
  }
}

@OptIn(ExperimentalJewelApi::class)
@Composable
private fun TranslatorDropdown(
  modifier: Modifier = Modifier,
  translators: List<AbstractTranslator>,
  selectedTranslator: AbstractTranslator?,
  onTranslatorSelected: (AbstractTranslator) -> Unit,
) {
  val resolvedIndex = selectedTranslator?.let { current ->
    val index = translators.indexOf(current)
    if (index >= 0) index else 0
  } ?: 0

  ListComboBox(
    modifier = modifier,
    items = translators,
    selectedIndex = resolvedIndex,
    onSelectedItemChange = { index ->
      translators.getOrNull(index)?.let(onTranslatorSelected)
    },
    itemKeys = { _, translator -> translator.key },
  ) { translator, _, _ ->
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = LayoutArrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      SwingIcon(icon = translator.icon)
      Text(translator.name)
    }
  }
}

@Composable
private fun HorizontalDivider() {
  Divider(
    orientation = org.jetbrains.jewel.ui.Orientation.Horizontal,
    color = JewelTheme.globalColors.borders.normal,
    modifier = Modifier.fillMaxWidth()
  )
}
