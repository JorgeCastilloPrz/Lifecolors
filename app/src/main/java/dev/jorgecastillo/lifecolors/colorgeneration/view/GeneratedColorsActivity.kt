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
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.detail.view.BottomCutout.Companion.DEFAULT_COLOR
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
import kotlinx.android.synthetic.main.activity_generated_colors.dot
import kotlinx.android.synthetic.main.activity_generated_colors.shadesTitle
import kotlinx.android.synthetic.main.activity_generated_colors.tintsList
import kotlinx.android.synthetic.main.activity_generated_colors.tintsTitle

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
    generateColors(selectedColor)
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
    launch(this, view, colorDetails.color, position)
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
