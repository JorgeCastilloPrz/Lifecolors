package dev.jorgecastillo.lifecolors.detail.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.fadeIn

internal class BottomCutout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  companion object {
    private val DEFAULT_COLOR = Color.parseColor("#FFE0B2")
  }

  init {
    val attributes = context.obtainStyledAttributes(attrs, R.styleable.BottomCutout)
    val roundedCorners = attributes.getBoolean(R.styleable.BottomCutout_roundedCorners, true)
    attributes.recycle()

    if (roundedCorners) {
      setBackgroundResource(R.drawable.cutout_bottom_sheet_bg)
      inflate(context, R.layout.cutout_expand_icon, this)
    } else {
      setBackgroundResource(R.drawable.cutout_bottom_sheet_bg_squared)
    }

    orientation = HORIZONTAL

    elevation = resources.getDimensionPixelSize(R.dimen.cutout_bottom_sheet_elevation)
      .toFloat()
    val padding = resources.getDimensionPixelSize(R.dimen.spacing)
    val paddingEnd = resources.getDimensionPixelSize(R.dimen.spacing_small)
    setPadding(padding, padding, paddingEnd, padding)
    gravity = Gravity.CENTER_VERTICAL
  }

  private fun addDot(@ColorInt color: Int = DEFAULT_COLOR) {
    val dot = Dot(context)
    val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
    dot.color = color
    val endMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)
    addView(dot, LayoutParams(dotSize, dotSize).apply { marginEnd = endMargin })
  }

  fun showPalette(bitmap: Bitmap) {
    Palette.from(bitmap)
      .generate { palette ->
        palette?.let {
          val lightVibrant = it.getLightVibrantColor(DEFAULT_COLOR)
          val vibrant = it.getVibrantColor(DEFAULT_COLOR)
          val darkVibrant = it.getDarkVibrantColor(DEFAULT_COLOR)
          val lightMuted = it.getLightMutedColor(DEFAULT_COLOR)
          val muted = it.getMutedColor(DEFAULT_COLOR)
          val darkMuted = it.getDarkMutedColor(DEFAULT_COLOR)

          addDot(lightVibrant)
          addDot(vibrant)
          addDot(darkVibrant)
          animateIn()
        }
      }
  }

  private fun animateIn() {
    val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
    val endMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)
    val padding = resources.getDimensionPixelSize(R.dimen.spacing)
    val paddingEnd = resources.getDimensionPixelSize(R.dimen.spacing_small)
    val shift = getChildAt(0).width + endMargin + dotSize * 3 + endMargin * 3 + padding + paddingEnd

    x += shift
    fadeIn()
    animate()
      .translationX(0f)
      .setDuration(300)
      .setInterpolator(DecelerateInterpolator(1.5f))
      .start()
  }
}
