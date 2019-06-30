package dev.jorgecastillo.lifecolors

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import dev.jorgecastillo.lifecolors.detail.view.OverlayTouchPopup

internal class Overlay @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  init {
    setWillNotDraw(true)
  }
  
  fun showTouchPopup() {
    removeAllViews()

    val randomX = (width * 0.15).toInt()
    val randomY = (height * 0.35).toInt()

    val touchPopup = OverlayTouchPopup(context)
    addView(touchPopup, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
      leftMargin = randomX
      topMargin = randomY
    })
    touchPopup.show(ContextCompat.getColor(context, R.color.white_overlay))
  }
}
