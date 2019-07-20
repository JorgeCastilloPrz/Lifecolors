package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.max

fun Int.toRGB() = "${Color.red(this)} / ${Color.green(this)} / ${Color.blue(this)}"
fun Int.toHex() = String.format("#%06X", 0xFFFFFF and this)
fun Int.toHexPureValue() = toHex().drop(1)
fun Int.toHSL() = this.let { color ->
  FloatArray(3).apply { ColorUtils.colorToHSL(color, this) }.let {
    "${String.format("%.2f", it[0])}º / ${String.format("%.2f", it[1])} / ${String.format("%.2f", it[2])}"
  }
}

/**
 * Formula extracted from {@see https://www.rapidtables.com/convert/color/rgb-to-cmyk.html}.
 */
fun Int.toCMYK(): String {
  val r = Color.red(this)
  val g = Color.green(this)
  val b = Color.blue(this)

  val r1 = r / 255f
  val g1 = g / 255f
  val b1 = b / 255f

  val k = 1.0f - max(r1, max(g1, b1))

  val cyan = (1.0f - r1 - k) / (1.0f - k)
  val magenta = (1.0f - g1 - k) / (1.0f - k)
  val yellow = (1.0f - b1 - k) / (1.0f - k)
  return "${String.format("%.2f", cyan)} / ${String.format("%.2f", magenta)} / ${String.format(
    "%.2f",
    yellow
  )} / ${String.format("%.2f", k)}"
}
