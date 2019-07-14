package dev.jorgecastillo.lifecolors.colorgeneration.view

import androidx.core.graphics.ColorUtils

fun Int.getShades(): List<Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)

  return IntProgression.fromClosedRange(50, 0, -5).mapIndexed { index, i ->
    colorHSL[2] = if (index == 0) {
      colorHSL[2]
    } else {
      i / 100f
    }
    ColorUtils.HSLToColor(colorHSL)
  }
}
