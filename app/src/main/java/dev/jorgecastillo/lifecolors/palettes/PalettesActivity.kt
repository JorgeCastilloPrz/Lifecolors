package dev.jorgecastillo.lifecolors.palettes

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.colorgeneration.view.GeneratedColorsActivity
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewModel
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Colors
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Error
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Idle
import dev.jorgecastillo.lifecolors.palettes.presentation.generatedColors
import dev.jorgecastillo.lifecolors.palettes.presentation.hasNoPickedColors
import dev.jorgecastillo.lifecolors.palettes.presentation.pickedColors
import dev.jorgecastillo.lifecolors.utils.SimpleTransitionListener
import kotlinx.android.synthetic.main.activity_palettes.bottomCutout
import kotlinx.android.synthetic.main.activity_palettes.content
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

  private lateinit var pickedColorsAdapter: PaletteColorsAdapter
  private lateinit var paletteColorsAdapter: PaletteColorsAdapter
  private lateinit var palettesViewModel: PalettesViewModel

  private fun pickedColors() = intent?.extras?.getIntegerArrayList(PICKED_COLORS) ?: arrayListOf()
  private fun generatedColors() = intent?.extras?.getIntegerArrayList(GENERATED_COLORS) ?: arrayListOf()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_palettes)
    setupEnterAnimation()
    setupViewModel()
    setupCutoutClicks()
    setupPickedColorsList()
    setupGeneratedColorsList()
  }

  private fun setupViewModel() {
    palettesViewModel = PalettesViewModel()
  }

  override fun onResume() {
    super.onResume()
    window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide_bottom)
    window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide_top)
  }

  private fun setupEnterAnimation() {
    window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
      override fun onTransitionEnd(transition: Transition) {
        observeViewStateChanges()
        fillUpFavStates()
      }
    })

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
    pickedColorsAdapter = PaletteColorsAdapter(colorClickListener(), favClickListener())
    pickedColorsList.adapter = pickedColorsAdapter
    val dividerDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
      setDrawable(ContextCompat.getDrawable(this@PalettesActivity, R.drawable.color_divider)!!)
    }
    pickedColorsList.addItemDecoration(dividerDecorator)
    val pickedColors = pickedColors()
    pickedColorsAdapter.colors = pickedColors.toList().map { it.toColorDetails() }
    if (pickedColors.isEmpty()) {
      pickedColorsCard.visibility = GONE
    }
  }

  private fun setupGeneratedColorsList() {
    generatedColorsList.setHasFixedSize(true)
    generatedColorsList.layoutManager = LinearLayoutManager(this)
    paletteColorsAdapter = PaletteColorsAdapter(colorClickListener(), favClickListener())
    generatedColorsList.adapter = paletteColorsAdapter
    val dividerDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
      setDrawable(ContextCompat.getDrawable(this@PalettesActivity, R.drawable.color_divider)!!)
    }
    generatedColorsList.addItemDecoration(dividerDecorator)
    paletteColorsAdapter.colors = generatedColors().toList().map { it.toColorDetails() }
  }

  private fun colorClickListener(): (View, ColorViewState, Int) -> Unit = { view, details, position ->
    window.enterTransition = null
    window.exitTransition = null
    GeneratedColorsActivity.launch(this, view, details.color, position)
  }

  private fun favClickListener(): (View, ColorViewState, Int) -> Unit = { view, details, position ->

  }

  private fun observeViewStateChanges() {
    palettesViewModel.state.observe(this, Observer { state -> render(state) })
  }

  private fun render(state: PalettesViewState) {
    when (state) {
      is Idle -> {
      }
      is Error -> {
        Snackbar.make(content, R.string.loading_fav_statuses_error, Snackbar.LENGTH_SHORT).show()
      }
      is Colors -> {
        if (state.hasNoPickedColors()) {
          pickedColorsCard.visibility = GONE
        } else {
          pickedColorsCard.visibility = VISIBLE
          pickedColorsAdapter.colors = state.pickedColors()
        }
        paletteColorsAdapter.colors = state.generatedColors()
      }
    }
  }

  private fun fillUpFavStates() {
    palettesViewModel.onColorsFavStateRequired(pickedColors(), generatedColors())
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
