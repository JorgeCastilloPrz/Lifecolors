package dev.jorgecastillo.lifecolors.detail.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import dev.jorgecastillo.lifecolors.R

internal class OverlayTouchPopup @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  private val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)

  init {
    orientation = VERTICAL
    clipChildren = false
    clipToPadding = false
    clipToOutline = false
    val padding = resources.getDimensionPixelSize(R.dimen.spacing_small)
    setPadding(padding, padding, padding, padding)
    addTextBubble()
  }

  private fun addTextBubble() {
    inflate(context, R.layout.overlay_touch_popup_bubble, this)
  }

  fun show(@ColorInt color: Int) {
    val dot = Dot(context).apply {
      this.color = color
    }
    addView(dot, LayoutParams(dotSize, dotSize).apply {
      topMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)
    })
    animateDot(dot)
  }

  private fun animateDot(dot: Dot) {
    val anim = AnimationUtils.loadAnimation(context, R.anim.dot_bounce)
    anim.startOffset = 50L
    dot.startAnimation(anim)
  }
}
