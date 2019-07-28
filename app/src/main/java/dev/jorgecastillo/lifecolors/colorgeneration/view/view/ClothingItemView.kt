package dev.jorgecastillo.lifecolors.colorgeneration.view.view

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import dev.jorgecastillo.lifecolors.R

class ClothingItemView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = android.R.attr.progressBarStyle
) : CardView(context, attrs, defStyleAttr) {

  @Override
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val height = measuredHeight

    val screenWidth = resources.displayMetrics.widthPixels
    val recyclerHorizontalPadding = resources.getDimensionPixelSize(R.dimen.spacing_small)
    val newWidth = (screenWidth - 12 - recyclerHorizontalPadding * 2) / 2

    setMeasuredDimension(newWidth, height)
    super.onMeasure(MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY), heightMeasureSpec)
  }
}
