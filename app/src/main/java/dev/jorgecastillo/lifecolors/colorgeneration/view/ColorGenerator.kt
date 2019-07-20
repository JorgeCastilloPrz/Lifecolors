package dev.jorgecastillo.lifecolors.colorgeneration.view

import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

fun Int.getShades(): List<Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)

  val start = (colorHSL[2] * 10000000).roundToInt()
  val step = if (start > 0) {
    -1 * start / 10
  } else 1
  return IntProgression.fromClosedRange(start, 0, step).map { i ->
    colorHSL[2] = i / 10000000f
    ColorUtils.HSLToColor(colorHSL)
  }
}

fun Int.getTints(): List<Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)

  val start = (colorHSL[2] * 10000000).roundToInt()
  val step = if (start < 10000000) (10000000 - start) / 10 else 1
  return IntProgression.fromClosedRange(start, 10000000, step).map { i ->
    colorHSL[2] = i / 10000000f
    ColorUtils.HSLToColor(colorHSL)
  }
}

/**
 * The Hue is the colour's position on the colour wheel, expressed in degrees from 0° to 359°, representing the 360° of
 * the wheel; 0° being red, 180° being red's opposite colour cyan, and so on. The complimentary color stands for the
 * color in the opposite side of the circle, so it's (hue + 180) % 360.
 */
fun Int.complimentary(): Int {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)
  val hue = colorHSL[0] // 0° to 359°
  val complimentaryHue = (hue + 180) % 360
  colorHSL[0] = complimentaryHue
  return ColorUtils.HSLToColor(colorHSL)
}

/**
 * The Hue is the colour's position on the colour wheel, expressed in degrees from 0° to 359°, representing the 360° of
 * the wheel; 0° being red, 180° being red's opposite colour cyan, and so on. The triadic colors stand for 3 colors that
 * compose a perfect triangle (equal sides) over the circle, so they are equally far from each other.
 *
 * Triadic colors for h0 would be (hue + 120) % 360 and (hue + 240) % 360.
 */
fun Int.triadic(): Pair<Int, Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)
  val hue = colorHSL[0] // 0° to 359°

  val h1 = colorHSL.copyOf()
  val h2 = colorHSL.copyOf()

  h1[0] = (hue + 120) % 360
  h2[0] = (hue + 240) % 360
  return Pair(ColorUtils.HSLToColor(h1), ColorUtils.HSLToColor(h2))
}

/**
 * The Hue is the colour's position on the colour wheel, expressed in degrees from 0° to 359°, representing the 360° of
 * the wheel; 0° being red, 180° being red's opposite colour cyan, and so on. The tetradic colors stand for 4 colors
 * that compose a perfect square (equal sides) over the circle, so they are equally far from each other.
 *
 * Tetradic colors for h0 would be (hue + 90) % 360, (hue + 180) % 360 and (hue + 270) % 360.
 */
fun Int.tetradic(): Triple<Int, Int, Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)
  val hue = colorHSL[0] // 0° to 359°

  val h1 = colorHSL.copyOf()
  val h2 = colorHSL.copyOf()
  val h3 = colorHSL.copyOf()

  h1[0] = (hue + 90) % 360
  h2[0] = (hue + 180) % 360
  h3[0] = (hue + 270) % 360

  return Triple(ColorUtils.HSLToColor(h1), ColorUtils.HSLToColor(h2), ColorUtils.HSLToColor(h3))
}

/**
 * The Hue is the colour's position on the colour wheel, expressed in degrees from 0° to 359°, representing the 360° of
 * the wheel; 0° being red, 180° being red's opposite colour cyan, and so on. The analogous colors stand for 3 colors
 * that are together in the circle, separated by 30 degrees each.
 *
 * Analogous colors for h0 would be (hue + 30) % 360 & (hue - 30) % 360.
 */
fun Int.analogous(): Pair<Int, Int> {
  val colorHSL = FloatArray(3)
  ColorUtils.colorToHSL(this, colorHSL)
  val hue = colorHSL[0] // 0° to 359°

  val h1 = colorHSL.copyOf()
  val h2 = colorHSL.copyOf()

  h1[0] = (hue + 30) % 360
  h2[0] = (hue + 330) % 360

  return Pair(ColorUtils.HSLToColor(h1), ColorUtils.HSLToColor(h2))
}
