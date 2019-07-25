package dev.jorgecastillo.lifecolors.common.view.menu

import android.content.Context
import android.widget.ProgressBar
import dev.jorgecastillo.lifecolors.R

class MenuItemProgressCircle constructor(context: Context) : ProgressBar(context) {

  init {
    val verticalPadding = resources.getDimensionPixelSize(R.dimen.spacing)
    setPadding(0, verticalPadding, 0, verticalPadding)
  }
}
