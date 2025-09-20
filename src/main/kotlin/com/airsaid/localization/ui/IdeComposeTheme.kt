package com.airsaid.localization.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import java.awt.Color as AwtColor
import javax.swing.UIManager

@Composable
fun IdeTheme(content: @Composable () -> Unit) {
    val isDark = UIUtil.isUnderDarcula()

    val panelBackground = UIUtil.getPanelBackground().toComposeColor()
    val surfaceColor = getUiColor("Panel.background", UIUtil.getPanelBackground()).toComposeColor()
    val surfaceVariant = getUiColor("EditorPane.background", UIUtil.getPanelBackground()).toComposeColor()
    val foreground = UIUtil.getLabelForeground().toComposeColor()
    val secondaryForeground = UIUtil.getInactiveTextColor().toComposeColor()
    val outline = getUiColor("Component.borderColor", JBColor(0xD7DCE2, 0x3C3F41)).toComposeColor()
    val accent = JBColor(0x3574F0, 0x7EA7FF).toComposeColor()

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accent,
            onPrimary = Color.White,
            secondary = accent,
            onSecondary = Color.White,
            background = panelBackground,
            onBackground = foreground,
            surface = panelBackground,
            onSurface = foreground,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = secondaryForeground,
            outline = outline,
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = Color.White,
            secondary = accent,
            onSecondary = Color.White,
            background = panelBackground,
            onBackground = foreground,
            surface = panelBackground,
            onSurface = foreground,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = secondaryForeground,
            outline = outline,
        )
    }

    val typography = buildIdeTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content,
    )
}

private fun buildIdeTypography(): Typography {
    val baseFont = UIManager.getFont("Label.font") ?: java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13)
    val baseSize = baseFont.size2D

    fun style(multiplier: Float, weight: FontWeight = FontWeight.Normal) = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = weight,
        fontSize = (baseSize * multiplier).sp,
    )

    return Typography(
        bodyLarge = style(1.05f),
        bodyMedium = style(1f),
        bodySmall = style(0.9f),
        titleLarge = style(1.3f, FontWeight.SemiBold),
        titleMedium = style(1.15f, FontWeight.SemiBold),
        titleSmall = style(1f, FontWeight.Medium),
        headlineSmall = style(1.4f, FontWeight.SemiBold),
        labelMedium = style(0.9f, FontWeight.Medium),
        labelSmall = style(0.85f, FontWeight.Medium),
    )
}

private fun getUiColor(key: String, fallback: AwtColor): AwtColor = UIManager.getColor(key) ?: fallback

private fun AwtColor.toComposeColor(): Color = Color(red / 255f, green / 255f, blue / 255f, alpha / 255f)

private fun JBColor.toComposeColor(): Color = Color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
