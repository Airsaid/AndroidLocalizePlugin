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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.airsaid.localization.translate.AbstractTranslator
import com.airsaid.localization.translate.services.TranslatorService
import com.airsaid.localization.ui.IdeTheme
import com.airsaid.localization.ui.SupportedLanguagesDialog
import com.airsaid.localization.ui.components.IdeSwitch
import com.airsaid.localization.ui.components.IdeTextField
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.ui.UIUtil
import java.awt.Dimension
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.JComponent
import kotlin.math.max
import androidx.compose.foundation.layout.Arrangement as LayoutArrangement

/**
 * Compose implementation of the settings panel exposed through the IDE Settings.
 */
class SettingsComponent {
  companion object {
    private val LOG = Logger.getInstance(SettingsComponent::class.java)
  }

  private val composePanel = ComposePanel()

  private val translatorsState = mutableStateListOf<AbstractTranslator>()
  private val selectedTranslatorState = mutableStateOf<AbstractTranslator?>(null)
  private val enableCacheState = mutableStateOf(true)
  private val maxCacheSizeState = mutableStateOf("500")
  private val translationIntervalState = mutableStateOf("500")

  init {
    composePanel.preferredSize = Dimension(680, 560)
    composePanel.isOpaque = true
    composePanel.background = UIUtil.getPanelBackground()
    composePanel.setContent {
      IdeTheme {
        SettingsContent(
          translators = translatorsState,
          selectedTranslator = selectedTranslatorState.value,
          defaultTranslatorKey = TranslatorService.getInstance().getDefaultTranslator().key,
          enableCacheState = enableCacheState,
          maxCacheSizeState = maxCacheSizeState,
          translationIntervalState = translationIntervalState,
          onTranslatorSelected = { translator -> applySelectedTranslator(translator) },
          onShowSupportedLanguages = { translator -> SupportedLanguagesDialog(translator).show() },
          onConfigureTranslator = { translator ->
            TranslatorConfigurationManager.showConfigurationDialog(translator)
          }
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
private val TranslatorDropdownWidth = 280.dp
private val CompactFieldHeight = 36.dp
private val FormContentSpacing = 8.dp
private const val MAX_REQUEST_INTERVAL_MS = 60_000
private const val DONATION_URL =
  "https://github.com/Airsaid/AndroidLocalizePlugin/blob/master/README.md#support-and-donations"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
  translators: SnapshotStateList<AbstractTranslator>,
  selectedTranslator: AbstractTranslator?,
  defaultTranslatorKey: String?,
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
      .background(MaterialTheme.colorScheme.background)
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
            modifier = Modifier.width(TranslatorDropdownWidth),
            translators = translators,
            selectedTranslator = selectedTranslator,
            defaultTranslatorKey = defaultTranslatorKey,
            onTranslatorSelected = onTranslatorSelected
          )

          selectedTranslator?.let { translator ->
            if (TranslatorConfigurationManager.hasConfiguration(translator)) {
              IconButton(onClick = { onConfigureTranslator(translator) }) {
                Icon(
                  imageVector = Icons.Filled.Settings,
                  contentDescription = "Configure translator",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        selectedTranslator?.let { translator ->
          TextButton(
            onClick = { onShowSupportedLanguages(translator) },
            modifier = Modifier.align(Alignment.Start)
          ) {
            Text("See supported languages")
          }
        }
      }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

    SectionHeader(title = "Caching")

    SettingsFormRow(label = "Use cache") {
      IdeSwitch(
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
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        enabled = enableCacheState.value
      )
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

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
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        suffix = {
          Text(
            text = "ms",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
          )
        }
      )
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

    SectionHeader(title = "Donation")
    DonationSection()
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
        horizontalArrangement = LayoutArrangement.spacedBy(FormContentSpacing),
        content = content
      )
    }

    helperText?.let {
      Text(
        text = it,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface
    )

    TextButton(
      onClick = { BrowserUtil.browse(DONATION_URL) },
      modifier = Modifier.align(Alignment.Start)
    ) {
      Text("Buy me a coffee  ☕")
    }
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
  val selectedName = selectedTranslator?.name.orEmpty()

  ExposedDropdownMenuBox(
    modifier = modifier,
    expanded = expanded,
    onExpandedChange = { expanded = !expanded }
  ) {
    OutlinedTextField(
      modifier = Modifier
        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        .fillMaxWidth()
        .heightIn(min = CompactFieldHeight),
      value = selectedName,
      onValueChange = {},
      readOnly = true,
      label = { Text("Translator") },
      placeholder = { Text("Select translator") },
      singleLine = true,
      leadingIcon = {
        selectedPainter?.let {
          Icon(
            painter = it,
            contentDescription = selectedTranslator.name,
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
