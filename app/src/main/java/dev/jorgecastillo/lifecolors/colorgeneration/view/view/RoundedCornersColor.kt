package dev.jorgecastillo.lifecolors.colorgeneration.view.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.google.android.material.shape.MaterialShapeDrawable
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.detail.view.BottomCutout.Companion.DEFAULT_COLOR

class RoundedCornersColor @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  init {
    background = MaterialShapeDrawable().apply {
      setCornerRadius(resources.getDimensionPixelSize(R.dimen.card_corner_radius).toFloat())
      val attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundedCornersColor)
      val color = attributes.getColor(R.styleable.RoundedCornersColor_color, DEFAULT_COLOR)
      attributes.recycle()
      fillColor = ColorStateList.valueOf(color)
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, widthMeasureSpec)
  }

  fun setColor(@ColorInt color: Int) {
    (background as MaterialShapeDrawable).fillColor = ColorStateList.valueOf(color)
  }
}
