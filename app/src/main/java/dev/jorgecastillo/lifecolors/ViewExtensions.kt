package dev.jorgecastillo.lifecolors

import android.view.View

fun View.fadeOut() {
  this.animate()
    .alpha(0f)
    .setDuration(150)
    .start()
}

fun View.fadeIn(duration: Long = 150) {
  this.animate()
    .alpha(1f)
    .setDuration(duration)
    .start()
}
