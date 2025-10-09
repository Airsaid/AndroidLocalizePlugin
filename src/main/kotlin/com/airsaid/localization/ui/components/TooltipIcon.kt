package com.airsaid.localization.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.intellij.icons.AllIcons
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip

/**
 * Renders a context-help icon that reveals the provided message inside a tooltip when hovered.
 *
 * @param text Human-readable description shown inside the tooltip popup.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TooltipIcon(text: String) {
  Tooltip(tooltip = { Text(text) }) {
    SwingIcon(
      icon = AllIcons.General.ContextHelp,
      modifier = Modifier.size(16.dp)
    )
  }
}
