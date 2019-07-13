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
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jorgecastillo.lifecolors.R
import kotlinx.android.synthetic.main.activity_palettes.bottomCutout
import kotlinx.android.synthetic.main.activity_palettes.generatedColorsList
import kotlinx.android.synthetic.main.activity_palettes.pickedColorsCard
import kotlinx.android.synthetic.main.activity_palettes.pickedColorsList
import kotlinx.android.synthetic.main.cutout_content_palette_activity.toolbarIcon

class PalettesActivity : AppCompatActivity() {

  companion object {

    private const val PICKED_COLORS = "PICKED_COLORS"
    private const val GENERATED_COLORS = "GENERATED_COLORS"

    fun launch(
      source: Activity,
      sharedElement: View,
      pickedColors: List<Int>,
      generatedColors: List<Int>
    ) {
      val bundle = Bundle().apply {
        putIntegerArrayList(PICKED_COLORS, ArrayList(pickedColors))
        putIntegerArrayList(GENERATED_COLORS, ArrayList(generatedColors))
      }

      val intent = Intent(source, PalettesActivity::class.java)
      intent.putExtras(bundle)

      val options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(source, sharedElement, sharedElement.transitionName)
      ActivityCompat.startActivity(source, intent, options.toBundle())
    }
  }

  private lateinit var pickedColorsAdapter: GeneratedColorsAdapter
  private lateinit var generatedColorsAdapter: GeneratedColorsAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_palettes)
    setupEnterAnimation()
    setupCutoutClicks()
    setupPickedColorsList()
    setupGeneratedColorsList()
    fillUpColors()
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

  private fun setupCutoutClicks() {
    toolbarIcon.setOnClickListener { onBackPressed() }
  }

  private fun setupPickedColorsList() {
    pickedColorsList.setHasFixedSize(true)
    pickedColorsList.layoutManager = LinearLayoutManager(this)
    pickedColorsAdapter = GeneratedColorsAdapter()
    pickedColorsList.adapter = pickedColorsAdapter
  }

  private fun setupGeneratedColorsList() {
    generatedColorsList.setHasFixedSize(true)
    generatedColorsList.layoutManager = LinearLayoutManager(this)
    generatedColorsAdapter = GeneratedColorsAdapter()
    generatedColorsList.adapter = generatedColorsAdapter
  }

  private fun fillUpColors() {
    intent?.extras?.getIntegerArrayList(PICKED_COLORS)?.let { colors ->
      if (colors.isEmpty()) {
        pickedColorsCard.visibility = View.GONE
      } else {
        pickedColorsCard.visibility = View.VISIBLE
        pickedColorsAdapter.colors = colors.toList().map { it.toColorDetails() }
      }
    }

    intent?.extras?.getIntegerArrayList(GENERATED_COLORS)?.let { colors ->
      generatedColorsAdapter.colors = colors.toList().map { it.toColorDetails() }
    }
  }

  override fun onBackPressed() {
    val totalDuration = window.sharedElementReturnTransition.duration
    animateStatusBarExit(totalDuration)
    animateCutoutCornerExit(totalDuration)
    animateCutoutContentExit()
    finishAfterTransition()
  }

  private fun animateStatusBarExit(totalDuration: Long) {
    val transparentColor = Color.TRANSPARENT
    val accentColor = ContextCompat.getColor(this, R.color.colorAccent)
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

  private fun animateCutoutContentExit() {
    bottomCutout.animateOut()
  }
}
