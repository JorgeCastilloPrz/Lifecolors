package dev.jorgecastillo.lifecolors.palettes

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.R.color
import kotlinx.android.synthetic.main.activity_palettes.bottomCutout

class PalettesActivity : AppCompatActivity() {

  companion object {

    fun launch(
      source: Activity,
      sharedElement: View
    ) {
      val intent = Intent(source, PalettesActivity::class.java)
      val options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(source, sharedElement, sharedElement.transitionName)
      ActivityCompat.startActivity(source, intent, options.toBundle())
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_palettes)
    setupEnterAnimation()
  }

  private fun setupEnterAnimation() {
    val totalDuration = window.sharedElementEnterTransition.duration
    animateCutoutEnter(totalDuration)
    animateStatusBarEnter(totalDuration)
  }

  private fun animateCutoutEnter(totalDuration: Long) {
    val cornerAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
      duration = totalDuration
      interpolator = window.sharedElementEnterTransition.interpolator
      addUpdateListener {
        bottomCutout.bindTransitionProcess(it.animatedValue as Float)
      }
    }
    cornerAnimation.start()
  }

  private fun animateStatusBarEnter(totalDuration: Long) {
    val transparentColor = Color.TRANSPARENT
    val accentColor = ContextCompat.getColor(this, R.color.colorAccent)
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), transparentColor, accentColor).apply {
      duration = totalDuration
      interpolator = window.sharedElementEnterTransition.interpolator
      addUpdateListener {
        window.statusBarColor = it.animatedValue as Int
      }
    }
    colorAnimation.start()
  }

  override fun onBackPressed() {
    val totalDuration = window.sharedElementReturnTransition.duration
    animateStatusBarExit(totalDuration)
    animateCutoutCornerExit(totalDuration)
    finishAfterTransition()
  }

  private fun animateStatusBarExit(totalDuration: Long) {
    val transparentColor = Color.TRANSPARENT
    val accentColor = ContextCompat.getColor(this, color.colorAccent)
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), accentColor, transparentColor).apply {
      duration = totalDuration / 5
      interpolator = window.sharedElementEnterTransition.interpolator
      addUpdateListener {
        window.statusBarColor = it.animatedValue as Int
      }
    }
    colorAnimation.start()
  }

  private fun animateCutoutCornerExit(totalDuration: Long) {
    val cornerAnimation = ValueAnimator.ofFloat(1f, 0f).apply {
      duration = totalDuration
      interpolator = window.sharedElementReturnTransition.interpolator
      addUpdateListener { bottomCutout.bindTransitionProcess(it.animatedValue as Float) }
    }
    cornerAnimation.start()
  }
}
