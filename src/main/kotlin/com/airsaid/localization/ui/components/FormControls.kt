package com.airsaid.localization.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.CheckboxRow
import org.jetbrains.jewel.ui.component.CircularProgressIndicator
import org.jetbrains.jewel.ui.component.ListComboBox
import org.jetbrains.jewel.ui.component.Text

private val CompactFieldHeight = 36.dp
private val CompactDropdownHeight = 32.dp
private val CompactDropdownWidth = 280.dp

/**
 * IntelliJ-styled text field wrapper that supports secure input and Compose slots.
 *
 * @author airsaid
 */
@Composable
@Suppress("LongParameterList")
fun IdeTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  placeholder: (@Composable (() -> Unit))? = null,
  leadingIcon: (@Composable (() -> Unit))? = null,
  trailingIcon: (@Composable (() -> Unit))? = null,
  prefix: (@Composable (() -> Unit))? = null,
  suffix: (@Composable (() -> Unit))? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  secureInput: Boolean = false,
  singleLine: Boolean = true,
) {
  val visualTransformation = if (secureInput) {
    PasswordVisualTransformation()
  } else {
    VisualTransformation.None
  }

  Row(
    modifier = modifier.heightIn(min = CompactFieldHeight),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    leadingIcon?.let {
      Box(modifier = Modifier.padding(end = 4.dp)) {
        it()
      }
    }
    prefix?.invoke()

    Box(modifier = Modifier.weight(1f)) {
      BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
          .fillMaxWidth()
          .background(JewelTheme.globalColors.panelBackground, RoundedCornerShape(4.dp))
          .border(1.dp, JewelTheme.globalColors.borders.normal, RoundedCornerShape(4.dp))
          .padding(horizontal = 8.dp, vertical = 6.dp),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = JewelTheme.defaultTextStyle.copy(color = JewelTheme.globalColors.text.normal),
        cursorBrush = SolidColor(JewelTheme.globalColors.text.normal),
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
          if (value.isEmpty() && placeholder != null) {
            placeholder()
          }
          innerTextField()
        }
      )
    }

    suffix?.invoke()
    trailingIcon?.let {
      Box(modifier = Modifier.padding(start = 4.dp)) {
        it()
      }
    }
  }
}

/**
 * Dropdown field that mirrors IntelliJ look and feel while showing a loading indicator.
 *
 * @author airsaid
 */
@OptIn(ExperimentalJewelApi::class)
@Composable
fun IdeDropdownField(
  value: String,
  options: List<String>,
  onOptionSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  placeholder: String? = null,
  loading: Boolean = false,
) {
  val dropdownEnabled = enabled && options.isNotEmpty() && !loading

  data class DropdownEntry(val label: String, val value: String?, val isPlaceholder: Boolean)

  val entries = buildList {
    val hasSelection = options.contains(value)

    if (!hasSelection) {
      val placeholderLabel = when {
        value.isNotBlank() -> value
        !placeholder.isNullOrEmpty() -> placeholder
        else -> ""
      }
      add(DropdownEntry(placeholderLabel, null, isPlaceholder = true))
    }

    options.forEach { option ->
      add(DropdownEntry(option, option, isPlaceholder = false))
    }
  }

  val selectedIndex = entries.indexOfFirst { entry -> entry.value == value }
  val resolvedIndex = selectedIndex.takeIf { it >= 0 } ?: 0

  Box(modifier = modifier.width(CompactDropdownWidth).height(CompactDropdownHeight)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      ListComboBox(
        items = entries,
        selectedIndex = resolvedIndex,
        modifier = Modifier.weight(1f),
        onSelectedItemChange = { index ->
          if (dropdownEnabled) {
            entries.getOrNull(index)?.let { entry ->
              if (!entry.isPlaceholder && entry.value != null) {
                onOptionSelected(entry.value)
              }
            }
          }
        },
        itemKeys = { _, entry -> entry.value ?: "__placeholder__" },
        enabled = dropdownEnabled,
      ) { entry, _, _ ->
        Text(entry.label, modifier = Modifier.padding(6.dp))
      }

      if (loading) {
        Spacer(modifier = Modifier.width(8.dp))
        CircularProgressIndicator(Modifier.size(16.dp))
      }
    }
  }
}

/**
 * Check box row with optional title and subtitle consistent with IDE visuals.
 *
 * @author airsaid
 */
@Composable
fun IdeCheckBox(
  checked: Boolean,
  onCheckedChange: (checked: Boolean) -> Unit,
  modifier: Modifier = Modifier,
  title: String? = null,
  subTitle: String? = null,
  enabled: Boolean = true,
) {
  CheckboxRow(
    checked = checked,
    onCheckedChange = onCheckedChange,
    modifier = modifier,
    enabled = enabled,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      if (!title.isNullOrEmpty()) {
        Text(
          text = title,
          color = if (enabled) JewelTheme.globalColors.text.normal else JewelTheme.globalColors.text.disabled
        )
      }

      if (!subTitle.isNullOrEmpty()) {
        Text(
          text = subTitle,
          color = if (enabled) JewelTheme.globalColors.text.info else JewelTheme.globalColors.text.disabled
        )
      }
    }
  }
}
