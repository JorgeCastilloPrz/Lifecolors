package dev.jorgecastillo.lifecolors.common.view.menu

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import dev.jorgecastillo.lifecolors.R

class MenuItemProgressCircle : ProgressBar {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  init {
    val verticalPadding = resources.getDimensionPixelSize(R.dimen.spacing)
    setPadding(0, verticalPadding, 0, verticalPadding)
  }
}
