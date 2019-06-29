package dev.jorgecastillo.lifecolors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.palette.graphics.Palette
import dev.jorgecastillo.lifecolors.detail.view.Dot
import kotlin.math.abs

internal class Overlay @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  init {
    setWillNotDraw(true)
  }

  private val dotRadius = resources.getDimensionPixelSize(R.dimen.dot_size) / 2
  private var detectedObjects: List<Triple<Float, Float, Int>> = listOf()
    set(value) {
      field = value
      removeAllViews()
      addDots(value)
    }

  fun generateRandomDots(bitmap: Bitmap) {
    detectedObjects = (1..30).map {
      val randomX = (dotRadius * 2..(width - dotRadius * 2))
        .fold(listOf<Int>()) { acc, i -> if (acc.none { abs(i - it) <= dotRadius * 3 }) acc + i else acc }
        .shuffled()
        .first()
      val randomY = (dotRadius * 2..(height - dotRadius * 2))
        .fold(listOf<Int>()) { acc, i -> if (acc.none { abs(i - it) <= dotRadius * 3 }) acc + i else acc }
        .shuffled()
        .first()

      val color = Palette.from(bitmap)
        .setRegion(randomX - dotRadius, randomY - dotRadius, randomX + dotRadius, randomY + dotRadius)
        .generate()
        .getDominantColor(Color.parseColor("#FFE0B2"))

      Triple(randomX.toFloat(), randomY.toFloat(), color)
    }
  }

  private fun addDots(dots: List<Triple<Float, Float, Int>>) {
    val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
    dots.forEach {
      val dot = Dot(context).apply {
        color = it.third
      }
      addView(dot, LayoutParams(dotSize, dotSize))
      dot.x = it.first
      dot.y = it.second
    }
  }
}
