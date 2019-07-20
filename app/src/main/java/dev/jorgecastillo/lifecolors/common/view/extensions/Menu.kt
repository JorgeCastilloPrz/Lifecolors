package dev.jorgecastillo.lifecolors.common.view.extensions

import android.view.Menu
import androidx.annotation.IdRes

fun Menu.hideAction(@IdRes itemId: Int) {
  val item = this.findItem(itemId)
  item.icon.alpha = 0
}

fun Menu.showAction(@IdRes itemId: Int) {
  val item = this.findItem(itemId)
  item.icon.alpha = 255
}
