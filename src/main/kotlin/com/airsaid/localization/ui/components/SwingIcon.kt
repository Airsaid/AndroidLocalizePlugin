package com.airsaid.localization.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import java.awt.image.BufferedImage
import javax.swing.Icon

/**
 * A icon component to show a swing [Icon] in Jetpack Compose UI.
 *
 * @author airsaid
 */
@Composable
fun SwingIcon(icon: Icon?, modifier: Modifier = Modifier) {
  if (icon == null) return
  val imageBitmap = remember(icon) { icon.toImageBitmap() }
  Image(
    painter = remember(imageBitmap) { BitmapPainter(imageBitmap) },
    contentDescription = null,
    modifier = modifier.size(20.dp),
  )
}

/**
 * A icon component to show a swing [Icon] in Jetpack Compose UI.
 *
 * @author airsaid
 */
@Composable
fun SwingIcon(icon: Icon?, modifier: Modifier = Modifier, tintColor: Color) {
  if (icon == null) return
  val imageBitmap = remember(icon) { icon.toImageBitmap() }
  Image(
    painter = remember(imageBitmap) { BitmapPainter(imageBitmap) },
    contentDescription = null,
    modifier = modifier.size(20.dp),
    colorFilter = ColorFilter.tint(tintColor)
  )
}

private fun Icon.toImageBitmap(): ImageBitmap {
  val image = BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_ARGB)
  val graphics = image.createGraphics()
  graphics.background = java.awt.Color(0, 0, 0, 0)
  paintIcon(null, graphics, 0, 0)
  graphics.dispose()
  return image.toComposeImageBitmap()
}