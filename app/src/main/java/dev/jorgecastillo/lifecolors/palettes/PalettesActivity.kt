package dev.jorgecastillo.lifecolors.palettes

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.Transition
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.fadeOut
import dev.jorgecastillo.lifecolors.utils.SimpleAnimatorListener
import dev.jorgecastillo.lifecolors.utils.SimpleTransitionListener
import kotlinx.android.synthetic.main.activity_palettes.bottomCutout
import kotlinx.android.synthetic.main.activity_palettes.toolbar

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
    window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
      override fun onTransitionEnd(transition: Transition) {
        transition.removeListener(this)
        bottomCutout.fadeOut()
      }
    })
  }

  override fun onBackPressed() {
    toolbar.animate().alpha(0f).setListener(object : SimpleAnimatorListener() {
      override fun onAnimationStart(animation: Animator?) {
        super.onAnimationStart(animation)
        bottomCutout.alpha = 1f
      }

      override fun onAnimationEnd(animation: Animator?) {
        finishAfterTransition()
      }
    }).start()
  }
}
