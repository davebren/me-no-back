package org.eski.ui.util

import androidx.compose.ui.graphics.Color


data class Hsl(
  val hue: Float,
  val saturation: Float,
  val lightness: Float,
)

fun Color.hsl(): Hsl {
  val max = maxOf(red, green, blue)
  val min = minOf(red, green, blue)
  val l = (max + min) / 2f
  if (max == min) {
    return Hsl(0f, 0f, l) // Achromatic case (gray)
  }
  val d = max - min
  val s = if (l > 0.5f) d / (2f - max - min) else d / (max + min)
  val h = when (max) {
    red -> (green - blue) / d + (if (green < blue) 6f else 0f)
    green -> (blue - red) / d + 2f
    blue -> (red - green) / d + 4f
    else -> 0f
  } * 60f
  return Hsl(h, s, l)
}