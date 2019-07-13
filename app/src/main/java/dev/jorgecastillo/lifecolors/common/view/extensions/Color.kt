package dev.jorgecastillo.lifecolors.common.view.extensions

import android.graphics.Color
import kotlin.math.max

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
