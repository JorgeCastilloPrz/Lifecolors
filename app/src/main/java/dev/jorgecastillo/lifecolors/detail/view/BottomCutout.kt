package dev.jorgecastillo.lifecolors.detail.view

import android.animation.LayoutTransition
import android.animation.LayoutTransition.APPEARING
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.common.view.model.Coordinates
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.PICKED
import dev.jorgecastillo.lifecolors.common.view.model.LifeColor
import dev.jorgecastillo.lifecolors.common.view.model.toGeneratedLifeColor
import dev.jorgecastillo.lifecolors.common.view.model.toPickedLifeColor
import dev.jorgecastillo.lifecolors.detail.view.drawable.CutoutDrawable
import dev.jorgecastillo.lifecolors.common.view.extensions.fadeIn
import kotlinx.android.synthetic.main.cutout_content_palette_activity.view.toolbarIcon
import kotlinx.android.synthetic.main.cutout_content_palette_activity.view.toolbarTitle
import kotlinx.android.synthetic.main.cutout_expand_icon.view.colorCount

internal class BottomCutout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  companion object {
    val DEFAULT_COLOR = Color.parseColor("#FFE0B2")
  }

  private val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
  private var generatedColors = setOf<LifeColor>()

  init {
    layoutTransition = LayoutTransition().apply {
      this.disableTransitionType(APPEARING)
    }
    clipToPadding = false
    clipChildren = false

    val attributes = context.obtainStyledAttributes(attrs, R.styleable.BottomCutout)
    val roundedCorners = attributes.getBoolean(R.styleable.BottomCutout_roundedCorners, true)
    attributes.recycle()

    if (roundedCorners) {
      background = CutoutDrawable(context)
      inflate(context, R.layout.cutout_expand_icon, this)
    } else {
      background = CutoutDrawable(context)
      inflate(context, R.layout.cutout_content_palette_activity, this)
    }

    orientation = HORIZONTAL

    elevation = resources.getDimensionPixelSize(R.dimen.cutout_bottom_sheet_elevation)
      .toFloat()
    val padding = resources.getDimensionPixelSize(R.dimen.spacing)
    val paddingEnd = resources.getDimensionPixelSize(R.dimen.spacing_small)
    setPadding(padding, padding, paddingEnd, padding)
    gravity = Gravity.CENTER_VERTICAL
  }

  private fun addDot(@ColorInt color: Int = DEFAULT_COLOR, addFirst: Boolean = false, delayedAlpha: Boolean = false) {
    val dot = Dot(context)
    val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
    dot.color = color
    val endMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)
    if (addFirst) {
      addView(dot, 1, LayoutParams(dotSize, dotSize).apply { marginEnd = endMargin })
    } else {
      addView(dot, LayoutParams(dotSize, dotSize).apply { marginEnd = endMargin })
    }
    if (delayedAlpha) {
      dot.alpha = 0f
      dot.postDelayed({ dot.alpha = 1f }, 280)
    }
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

          generatedColors = generatedColors + vibrant.toGeneratedLifeColor()
          generatedColors = generatedColors + darkVibrant.toGeneratedLifeColor()
          generatedColors = generatedColors + lightVibrant.toGeneratedLifeColor()
          generatedColors = generatedColors + lightMuted.toGeneratedLifeColor()
          generatedColors = generatedColors + muted.toGeneratedLifeColor()
          generatedColors = generatedColors + darkMuted.toGeneratedLifeColor()

          addDot(generatedColors.toList()[0].color)
          addDot(generatedColors.toList()[1].color)
          addDot(generatedColors.toList()[2].color)
          animateIn()
          colorCount.text = generatedColors.size.toString()
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

  fun animateOut() {
    toolbarIcon.animate().alpha(0f).setDuration(100).start()
    toolbarTitle.animate().alpha(0f).setDuration(100).start()
  }

  fun bindTransitionProcess(progress: Float) {
    (background as CutoutDrawable).bindProgress(progress)
  }

  fun pickedColors(): List<Int> = generatedColors.toList().filter { it.type == PICKED }.map { it.color }
  fun generatedColors(): List<Int> = generatedColors.toList().filter { it.type == GENERATED }.map { it.color }

  fun getFirstCirclePosition(): Coordinates {
    val endMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)
    val padding = resources.getDimensionPixelSize(R.dimen.spacing)
    return Coordinates(
      x = getChildAt(0).width + endMargin + padding + dotSize / 2f,
      y = getChildAt(1).y + dotSize / 2
    )
  }

  fun addDotFirst(dot: Dot) {
    generatedColors = setOf(dot.color.toPickedLifeColor()) + generatedColors
    addDot(dot.color, addFirst = true, delayedAlpha = true)
    bumpCounter()
  }

  @SuppressLint("SetTextI18n")
  private fun bumpCounter() {
    val counterText = getChildAt(0) as TextView
    counterText.text = "${Integer.parseInt(counterText.text.toString()) + 1}"
  }
}
