package dev.jorgecastillo.lifecolors

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.palette.graphics.Palette
import kotlin.math.abs

internal class Overlay @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  companion object {
    private const val STROKE_WIDTH = 8f
  }

  private val dotRadius = context.resources.getDimensionPixelSize(R.dimen.color_dot_radius)
  private var detectedObjects: List<Triple<Float, Float, Int>> = listOf()
    set(value) {
      field = value
      invalidate()
    }

  private val objectBoundaryPaint = Paint().apply {
    strokeWidth = STROKE_WIDTH
    color = Color.WHITE
    style = Paint.Style.STROKE
  }

  override fun draw(canvas: Canvas) {
    super.draw(canvas)
    detectedObjects.forEach {
      objectBoundaryPaint.color = Color.WHITE
      objectBoundaryPaint.style = Paint.Style.STROKE
      canvas.drawCircle(it.first, it.second, dotRadius.toFloat(), objectBoundaryPaint)
      objectBoundaryPaint.color = it.third
      objectBoundaryPaint.style = Paint.Style.FILL
      canvas.drawCircle(it.first, it.second, dotRadius.toFloat() - STROKE_WIDTH / 2, objectBoundaryPaint)
    }
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
}
