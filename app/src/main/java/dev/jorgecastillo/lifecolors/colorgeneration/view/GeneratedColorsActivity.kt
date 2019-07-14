package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.Transition
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.detail.view.BottomCutout.Companion.DEFAULT_COLOR
import dev.jorgecastillo.lifecolors.utils.GUIUtils
import dev.jorgecastillo.lifecolors.utils.OnRevealAnimationListener
import dev.jorgecastillo.lifecolors.utils.SimpleTransitionListener
import kotlinx.android.synthetic.main.activity_generated_colors.appBarLayout
import kotlinx.android.synthetic.main.activity_generated_colors.dot

class GeneratedColorsActivity : AppCompatActivity() {

  companion object {

    private const val PICKED_COLOR = "PICKED_COLORS"
    private const val PICKED_COLOR_POSITION = "PICKED_COLOR_POSITION"

    fun launch(
      source: Activity,
      sharedElement: View,
      pickedColor: Int,
      position: Int
    ) {
      val bundle = Bundle().apply {
        putInt(PICKED_COLOR, pickedColor)
        putInt(PICKED_COLOR_POSITION, position)
      }

      val intent = Intent(source, GeneratedColorsActivity::class.java)
      intent.putExtras(bundle)

      val options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(source, sharedElement, sharedElement.transitionName)
      ActivityCompat.startActivity(source, intent, options.toBundle())
    }
  }

  private fun selectedColor() = intent?.extras?.getInt(PICKED_COLOR, DEFAULT_COLOR) ?: DEFAULT_COLOR

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_generated_colors)
    postponeEnterTransition()

    val selectedColor = selectedColor()
    val position = intent?.extras?.getInt(PICKED_COLOR_POSITION, 0) ?: 0
    dot.color = selectedColor
    dot.transitionName = "$selectedColor$position"

    setupEnterAnimation(selectedColor)
    startPostponedEnterTransition()
  }

  private fun setupEnterAnimation(selectedColor: Int) {
    window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
      override fun onTransitionEnd(transition: Transition) {
        transition.removeListener(this)
        dot.strokeColor = Color.TRANSPARENT
        animateRevealShow(selectedColor)
      }
    })
  }

  private fun animateRevealShow(selectedColor: Int) {
    val cx = (appBarLayout.left + appBarLayout.right) / 2
    val cy = (appBarLayout.top + appBarLayout.bottom) / 2
    GUIUtils.animateRevealShow(appBarLayout, dot.width / 2, selectedColor,
      cx, cy, object : OnRevealAnimationListener {
        override fun onRevealHide() {
        }

        override fun onRevealShow() {
          // initViews()
        }
      })
  }

  override fun onBackPressed() {
    GUIUtils.animateRevealHide(
      appBarLayout,
      selectedColor(),
      ContextCompat.getColor(this, R.color.background),
      dot.width / 2,
      object : OnRevealAnimationListener {
        override fun onRevealHide() {
          backPressed()
        }

        override fun onRevealShow() {
        }
      })
  }

  fun backPressed() {
    finishAfterTransition()
  }
}
