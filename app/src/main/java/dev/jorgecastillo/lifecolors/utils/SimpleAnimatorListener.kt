package dev.jorgecastillo.lifecolors.utils

import android.animation.Animator
import android.animation.Animator.AnimatorListener

abstract class SimpleAnimatorListener : AnimatorListener {
  override fun onAnimationRepeat(animation: Animator?) {
  }

  override fun onAnimationEnd(animation: Animator?) {
  }

  override fun onAnimationCancel(animation: Animator?) {
  }

  override fun onAnimationStart(animation: Animator?) {
  }
}
