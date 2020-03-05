package dev.jorgecastillo.lifecolors.common.view

import android.transition.Transition
import kotlinx.android.synthetic.main.activity_detail.activityRoot

abstract class SimpleTransitionListener : Transition.TransitionListener {
  override fun onTransitionStart(transition: Transition) {
  }

  override fun onTransitionEnd(transition: Transition) {
  }

  override fun onTransitionCancel(transition: Transition) {
  }

  override fun onTransitionPause(transition: Transition) {
  }

  override fun onTransitionResume(transition: Transition) {
  }
}
