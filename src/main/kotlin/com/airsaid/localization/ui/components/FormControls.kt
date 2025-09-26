package com.airsaid.localization.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val CompactFieldHeight = 36.dp
private val CompactDropdownHeight = 32.dp

/**
 * IntelliJ-styled text field wrapper that supports secure input and Compose slots.
 *
 * @author airsaid
 */
@OptIn(ExperimentalMaterial3Api::class)
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

  val visualTransformation = if (secureInput) {
    PasswordVisualTransformation()
  } else {
    VisualTransformation.None
  }

  BasicTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier
      .heightIn(min = CompactFieldHeight)
      .defaultMinSize(minHeight = CompactFieldHeight),
    enabled = enabled,
    readOnly = readOnly,
    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
    visualTransformation = visualTransformation,
    singleLine = singleLine,
    keyboardOptions = keyboardOptions,
    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
    interactionSource = interactionSource,
  ) { innerTextField ->
    OutlinedTextFieldDefaults.DecorationBox(
      value = value,
      visualTransformation = visualTransformation,
      innerTextField = innerTextField,
      placeholder = placeholder,
      label = null,
      prefix = prefix,
      suffix = suffix,
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
          colors = colors,
        )
      }
    )
  }
}

/**
 * Dropdown field that mirrors IntelliJ look and feel while showing a loading indicator.
 *
 * @author airsaid
 */
@OptIn(ExperimentalMaterial3Api::class)
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
  var expanded by remember { mutableStateOf(false) }

  val dropdownEnabled = enabled && options.isNotEmpty() && !loading
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

  if (!dropdownEnabled && expanded) {
    expanded = false
  }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = {
      if (dropdownEnabled) {
        expanded = !expanded
      }
    }
  ) {
    val fieldModifier = modifier
      .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
      .fillMaxWidth()
      .heightIn(min = CompactDropdownHeight)
      .defaultMinSize(minHeight = CompactDropdownHeight)

    OutlinedTextField(
      value = value,
      onValueChange = {},
      modifier = fieldModifier,
      readOnly = true,
      singleLine = true,
      enabled = dropdownEnabled,
      textStyle = MaterialTheme.typography.bodyMedium,
      placeholder = placeholder?.let { placeholderText ->
        {
          Text(
            text = placeholderText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      trailingIcon = {
        if (loading) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp
          )
        } else {
          ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }
      },
      colors = colors,
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
      options.forEach { option ->
        DropdownMenuItem(
          text = { Text(option) },
          onClick = {
            expanded = false
            onOptionSelected(option)
          }
        )
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
  onValueChange: (checked: Boolean) -> Unit,
  modifier: Modifier = Modifier,
  title: String? = null,
  subTitle: String? = null,
  enabled: Boolean = true,
) {
  val toggleInteraction = remember { MutableInteractionSource() }
  Row(
    modifier = modifier.toggleable(
      value = checked,
      interactionSource = toggleInteraction,
      indication = null,
      role = Role.Checkbox,
      onValueChange = onValueChange
    ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    IdeCheckbox(checked = checked)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      if (!title.isNullOrEmpty()) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }

      if (!subTitle.isNullOrEmpty()) {
        Text(
          text = subTitle,
          style = MaterialTheme.typography.bodySmall,
          color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.6f
          )
        )
      }
    }
  }
}

/**
 * Compact checkbox indicator used within other form controls.
 *
 * @author airsaid
 */
@Composable
fun IdeCheckbox(
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
        imageVector = Icons.Filled.Check,
        contentDescription = null,
        tint = colors.onPrimary,
        modifier = Modifier.size(10.dp),
      )
    }
  }
}

/**
 * Toggle switch preconfigured to match IntelliJ color tokens.
 *
 * @author airsaid
 */
@Composable
fun IdeSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val scheme = MaterialTheme.colorScheme
  Switch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    enabled = enabled,
    modifier = modifier.scale(0.85f),
    colors = SwitchDefaults.colors(
      checkedThumbColor = scheme.onPrimary,
      checkedTrackColor = scheme.primary,
      checkedBorderColor = scheme.primary.copy(alpha = 0.2f),
      checkedIconColor = scheme.primary,
      uncheckedThumbColor = scheme.surface,
      uncheckedTrackColor = scheme.outline.copy(alpha = 0.6f),
      uncheckedBorderColor = scheme.outline.copy(alpha = 0.4f),
      uncheckedIconColor = scheme.onSurfaceVariant,
      disabledCheckedTrackColor = scheme.primary.copy(alpha = 0.3f),
      disabledCheckedThumbColor = scheme.onSurface.copy(alpha = 0.3f),
      disabledUncheckedTrackColor = scheme.onSurfaceVariant.copy(alpha = 0.2f),
      disabledUncheckedThumbColor = scheme.onSurface.copy(alpha = 0.2f),
    )
  )
}
