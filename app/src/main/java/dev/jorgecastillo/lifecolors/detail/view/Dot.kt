package dev.jorgecastillo.lifecolors.detail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt

internal class Dot @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  companion object {
    private const val STROKE_WIDTH = 8f
  }

  @ColorInt var color: Int = Color.WHITE

  private val objectBoundaryPaint = Paint().apply {
    strokeWidth = STROKE_WIDTH
    color = Color.WHITE
    style = Paint.Style.STROKE
    isAntiAlias = true
  }

  override fun draw(canvas: Canvas) {
    super.draw(canvas)
    objectBoundaryPaint.color = color
    objectBoundaryPaint.style = Paint.Style.FILL
    canvas.drawCircle(width / 2f, height / 2f, width / 2f - STROKE_WIDTH / 2, objectBoundaryPaint)
    objectBoundaryPaint.color = Color.WHITE
    objectBoundaryPaint.style = Paint.Style.STROKE
    canvas.drawCircle(width / 2f, height / 2f, width / 2f - STROKE_WIDTH, objectBoundaryPaint)
  }
}
