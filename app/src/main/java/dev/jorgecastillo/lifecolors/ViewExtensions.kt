package dev.jorgecastillo.lifecolors

import android.animation.Animator
import android.view.View
import dev.jorgecastillo.lifecolors.utils.SimpleAnimatorListener

fun View.fadeOut(onComplete: () -> Unit = {}) {
  this.animate()
    .alpha(0f)
    .setDuration(150)
    .setListener(object : SimpleAnimatorListener() {
      override fun onAnimationEnd(animation: Animator?) {
        super.onAnimationEnd(animation)
        onComplete()
      }
    })
    .start()
}

fun View.fadeIn(duration: Long = 150) {
  this.animate()
    .alpha(1f)
    .setDuration(duration)
    .start()
}
