package com.airsaid.localization.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val CompactFieldHeight = 36.dp

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
