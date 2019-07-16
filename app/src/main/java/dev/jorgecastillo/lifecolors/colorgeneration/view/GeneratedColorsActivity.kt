package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
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
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.detail.view.BottomCutout.Companion.DEFAULT_COLOR
import dev.jorgecastillo.lifecolors.fadeOut
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorDetails
import dev.jorgecastillo.lifecolors.palettes.toColorDetails
import dev.jorgecastillo.lifecolors.utils.GUIUtils
import dev.jorgecastillo.lifecolors.utils.OnRevealAnimationListener
import dev.jorgecastillo.lifecolors.utils.SimpleTransitionListener
import kotlinx.android.synthetic.main.activity_generated_colors.appBarLayout
import kotlinx.android.synthetic.main.activity_generated_colors.colorShadesList
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColor
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorBase
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorBaseHex
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorHex
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorTitle
import kotlinx.android.synthetic.main.activity_generated_colors.content
import kotlinx.android.synthetic.main.activity_generated_colors.dot
import kotlinx.android.synthetic.main.activity_generated_colors.shadesTitle
import kotlinx.android.synthetic.main.activity_generated_colors.tintsList
import kotlinx.android.synthetic.main.activity_generated_colors.tintsTitle

class GeneratedColorsActivity : AppCompatActivity() {

  companion object {

    private const val PICKED_COLOR = "PICKED_COLORS"
    private const val PICKED_COLOR_POSITION = "PICKED_COLOR_POSITION"

    fun launchWithNoTransition(
      source: Activity,
      pickedColor: Int
    ) {
      val bundle = Bundle().apply {
        putInt(PICKED_COLOR, pickedColor)
      }

      val intent = Intent(source, GeneratedColorsActivity::class.java)
      intent.putExtras(bundle)
      source.startActivity(intent)
    }

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

  private fun selectedPosition() = intent?.extras?.getInt(PICKED_COLOR_POSITION, -1) ?: -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_generated_colors)
    postponeEnterTransition()

    val selectedColor = selectedColor()
    val position = selectedPosition()
    dot.color = selectedColor
    dot.transitionName = "$selectedColor$position"

    if (position == -1) {
      dot.strokeColor = Color.TRANSPARENT
      appBarLayout.post {
        animateRevealShow(selectedColor)
        animateStatusBarEnter()
        generateColors(selectedColor)
      }
    } else {
      setupEnterAnimation(selectedColor)
      startPostponedEnterTransition()
      generateColors(selectedColor)
    }
  }

  private fun setupEnterAnimation(selectedColor: Int) {
    window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
      override fun onTransitionEnd(transition: Transition) {
        transition.removeListener(this)
        dot.strokeColor = Color.TRANSPARENT
        animateRevealShow(selectedColor)
        animateStatusBarEnter()
      }
    })
  }

  private fun animateStatusBarEnter() {
    val totalDuration = window.sharedElementEnterTransition.duration
    val transparentColor = Color.TRANSPARENT
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), transparentColor, selectedColor()).apply {
      duration = totalDuration
      interpolator = window.sharedElementEnterTransition.interpolator
      addUpdateListener {
        window.statusBarColor = it.animatedValue as Int
      }
    }
    colorAnimation.start()
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

  private fun generateColors(selectedColor: Int) {
    generateComplimentary(selectedColor)
    generateShades(selectedColor)
    generateTints(selectedColor)
  }

  private fun generateShades(selectedColor: Int) {
    val adapter = GeneratedColorsAdapter(onColorClickListener())
    colorShadesList.adapter = adapter
    colorShadesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    colorShadesList.setHasFixedSize(true)
    adapter.colors = selectedColor.getShades().map { it.toColorDetails() }

    val hexColor = String.format("#%06X", 0xFFFFFF and selectedColor)
    shadesTitle.text = resources.getString(R.string.shades, hexColor)
  }

  private fun generateTints(selectedColor: Int) {
    val adapter = GeneratedColorsAdapter(onColorClickListener())
    tintsList.adapter = adapter
    tintsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    tintsList.setHasFixedSize(true)
    adapter.colors = selectedColor.getTints().map { it.toColorDetails() }

    val hexColor = String.format("#%06X", 0xFFFFFF and selectedColor)
    tintsTitle.text = resources.getString(R.string.tints, hexColor)
  }

  private fun generateComplimentary(selectedColor: Int) {
    val hexColor = String.format("#%06X", 0xFFFFFF and selectedColor)
    complimentaryColorTitle.text = resources.getString(R.string.complimentary, hexColor)
    complimentaryColorBase.setBackgroundColor(selectedColor)
    complimentaryColor.setBackgroundColor(selectedColor.complimentary())

    val hexColorComplimentary = String.format("#%06X", 0xFFFFFF and selectedColor.complimentary())
    complimentaryColorBaseHex.text = hexColor
    complimentaryColorHex.text = hexColorComplimentary
  }

  private fun onColorClickListener(): (View, ColorDetails, Int) -> Unit = { view, colorDetails, position ->
    launchWithNoTransition(this, colorDetails.color)
  }

  override fun onBackPressed() {
    if (selectedPosition() == -1) {
      backPressed()
    } else {
      content.fadeOut()
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
  }

  fun backPressed() {
    window.setBackgroundDrawable(null)
    finishAfterTransition()
  }
}
