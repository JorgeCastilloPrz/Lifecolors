package dev.jorgecastillo.lifecolors.colorgeneration.view

import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

fun Int.getShades(): List<Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)

  val start = (colorHSL[2] * 10000000).roundToInt()
  return IntProgression.fromClosedRange(start, 0, -1 * start / 10).map { i ->
    colorHSL[2] = i / 10000000f
    ColorUtils.HSLToColor(colorHSL)
  }
}

fun Int.getTints(): List<Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)

  val start = (colorHSL[2] * 10000000).roundToInt()
  return IntProgression.fromClosedRange(start, 10000000, (10000000 - start) / 10).map { i ->
    colorHSL[2] = i / 10000000f
    ColorUtils.HSLToColor(colorHSL)
  }
}
