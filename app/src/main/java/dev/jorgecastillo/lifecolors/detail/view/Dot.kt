package dev.jorgecastillo.lifecolors.detail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import dev.jorgecastillo.lifecolors.R

class Dot @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  companion object {
    private const val STROKE_WIDTH = 4f
  }

  @ColorInt var color: Int = Color.WHITE

  private val objectBoundaryPaint = Paint().apply {
    strokeWidth = STROKE_WIDTH
    color = Color.WHITE
    style = Paint.Style.STROKE
    isAntiAlias = true
  }

  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.Dot)
    color = attributes.getColor(R.styleable.Dot_dotColor, Color.WHITE)
    objectBoundaryPaint.strokeWidth =
      attributes.getDimensionPixelSize(R.styleable.Dot_dotStrokeWidth, STROKE_WIDTH.toInt()).toFloat()
    attributes.recycle()
  }

  override fun draw(canvas: Canvas) {
    super.draw(canvas)
    objectBoundaryPaint.color = color
    objectBoundaryPaint.style = Paint.Style.FILL
    canvas.drawCircle(width / 2f, height / 2f, width / 2f - STROKE_WIDTH, objectBoundaryPaint)
    objectBoundaryPaint.color = Color.WHITE
    objectBoundaryPaint.style = Paint.Style.STROKE
    canvas.drawCircle(width / 2f, height / 2f, width / 2f - STROKE_WIDTH, objectBoundaryPaint)
  }
}
