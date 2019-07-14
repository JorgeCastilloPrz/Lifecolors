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

/**
 * The Hue is the colour's position on the colour wheel, expressed in degrees from 0° to 359°, representing the 360° of
 * the wheel; 0° being red, 180° being red's opposite colour cyan, and so on.
 */
fun Int.complimentary(): Int {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)
  val hue = colorHSL[0] // 0° to 359°
  val complimentaryHue = if (hue + 180 <= 359) hue + 180 else (180 - (359 - hue))
  colorHSL[0] = complimentaryHue
  return ColorUtils.HSLToColor(colorHSL)
}
