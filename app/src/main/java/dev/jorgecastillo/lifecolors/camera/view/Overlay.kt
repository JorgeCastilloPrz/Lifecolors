package dev.jorgecastillo.lifecolors.camera.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import dev.jorgecastillo.lifecolors.R.anim
import dev.jorgecastillo.lifecolors.R.color
import dev.jorgecastillo.lifecolors.R.dimen
import dev.jorgecastillo.lifecolors.common.view.extensions.fadeOut
import dev.jorgecastillo.lifecolors.detail.view.Dot
import dev.jorgecastillo.lifecolors.detail.view.OverlayTouchPopup

interface OnDotSelectedListener {
  fun onDotSelected(dot: Dot, x: Int, y: Int)
}

internal class Overlay @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  private val dotSize = resources.getDimensionPixelSize(dimen.dot_size)
  private var bitmap: Bitmap? = null
  var onDotSelectedListener: OnDotSelectedListener? = null

  init {
    setWillNotDraw(true)
  }

  private fun hideTouchPopup() {
    children.find { it is OverlayTouchPopup }?.fadeOut()
  }

  fun showTouchPopup() {
    removeAllViews()

    val popupX = (width * 0.15).toInt()
    val popupY = (height * 0.35).toInt()

    val touchPopup = OverlayTouchPopup(context)
    addView(touchPopup, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
      leftMargin = popupX
      topMargin = popupY
    })
    touchPopup.show(ContextCompat.getColor(context,
      color.white_overlay
    ))
  }

  fun adjustToBitmap(bitmap: Bitmap) {
    this.bitmap = bitmap
    layoutParams.width = bitmap.width
    layoutParams.height = bitmap.height
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x.toInt()
    val y = event.y.toInt()
    return when (event.action) {
      MotionEvent.ACTION_UP -> {
        hideTouchPopup()
        clearDots()
        showDot(x, y)
        true
      }
      else -> true
    }
  }

  private fun clearDots() {
    val dots = children.filter { it is Dot }
    dots.forEach {
      it.fadeOut(onComplete = { removeView(it) })
    }
    invalidate()
  }

  private fun showDot(x: Int, y: Int) {
    bitmap?.let { bmp ->
      val dotColor = bmp.getPixel(x, y)
      val dot = renderDot(x, y, dotColor)
      animateDot(dot)

      onDotSelectedListener?.onDotSelected(dot, x, y + top)
    }
  }

  private fun renderDot(x: Int, y: Int, dotColor: Int): Dot {
    val dot = Dot(context).apply {
      this.color = dotColor
    }
    addView(dot, LayoutParams(dotSize, dotSize).apply {
      topMargin = y - dotSize / 2
      leftMargin = x - dotSize / 2
    })
    return dot
  }

  private fun animateDot(dot: Dot) {
    val anim = AnimationUtils.loadAnimation(context,
      anim.dot_bounce
    )
    anim.startOffset = 50L
    dot.startAnimation(anim)
  }
}

