package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ColorGenerationViewModel
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ScreenViewState
import dev.jorgecastillo.lifecolors.common.view.extensions.copyToClipboard
import dev.jorgecastillo.lifecolors.common.view.extensions.hideAction
import dev.jorgecastillo.lifecolors.common.view.extensions.isDark
import dev.jorgecastillo.lifecolors.common.view.extensions.showAction
import dev.jorgecastillo.lifecolors.detail.view.BottomCutout.Companion.DEFAULT_COLOR
import dev.jorgecastillo.lifecolors.fadeIn
import dev.jorgecastillo.lifecolors.fadeOut
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.lifecolors.palettes.toColorDetails
import dev.jorgecastillo.lifecolors.utils.GUIUtils
import dev.jorgecastillo.lifecolors.utils.OnRevealAnimationListener
import dev.jorgecastillo.lifecolors.utils.SimpleTransitionListener
import kotlinx.android.synthetic.main.activity_generated_colors.analogousColor1
import kotlinx.android.synthetic.main.activity_generated_colors.analogousColor1Hex
import kotlinx.android.synthetic.main.activity_generated_colors.analogousColor2
import kotlinx.android.synthetic.main.activity_generated_colors.analogousColor2Hex
import kotlinx.android.synthetic.main.activity_generated_colors.analogousColorBase
import kotlinx.android.synthetic.main.activity_generated_colors.analogousColorBaseHex
import kotlinx.android.synthetic.main.activity_generated_colors.analogousColorsTitle
import kotlinx.android.synthetic.main.activity_generated_colors.appBarLayout
import kotlinx.android.synthetic.main.activity_generated_colors.colorShadesList
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColor
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorBase
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorBaseHex
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorHex
import kotlinx.android.synthetic.main.activity_generated_colors.complimentaryColorTitle
import kotlinx.android.synthetic.main.activity_generated_colors.content
import kotlinx.android.synthetic.main.activity_generated_colors.dot
import kotlinx.android.synthetic.main.activity_generated_colors.selectedColorHex
import kotlinx.android.synthetic.main.activity_generated_colors.selectedColorName
import kotlinx.android.synthetic.main.activity_generated_colors.shadesTitle
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColor1
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColor1Hex
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColor2
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColor2Hex
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColor3
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColor3Hex
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColorBase
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColorBaseHex
import kotlinx.android.synthetic.main.activity_generated_colors.tetradicColorsTitle
import kotlinx.android.synthetic.main.activity_generated_colors.tintsList
import kotlinx.android.synthetic.main.activity_generated_colors.tintsTitle
import kotlinx.android.synthetic.main.activity_generated_colors.toolbar
import kotlinx.android.synthetic.main.activity_generated_colors.triadicColor1
import kotlinx.android.synthetic.main.activity_generated_colors.triadicColor1Hex
import kotlinx.android.synthetic.main.activity_generated_colors.triadicColor2
import kotlinx.android.synthetic.main.activity_generated_colors.triadicColor2Hex
import kotlinx.android.synthetic.main.activity_generated_colors.triadicColorBase
import kotlinx.android.synthetic.main.activity_generated_colors.triadicColorBaseHex
import kotlinx.android.synthetic.main.activity_generated_colors.triadicColorsTitle

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
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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

  private lateinit var viewModel: ColorGenerationViewModel
  private lateinit var menu: Menu

  private fun selectedColor() = intent?.extras?.getInt(PICKED_COLOR, DEFAULT_COLOR) ?: DEFAULT_COLOR

  private fun selectedPosition() = intent?.extras?.getInt(PICKED_COLOR_POSITION, -1) ?: -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_generated_colors)
    postponeEnterTransition()
    setupStatusBar()

    val selectedColor = selectedColor()
    val headerTextColor = ContextCompat.getColor(
      this,
      if (selectedColor.isDark()) R.color.white else R.color.black
    )
    selectedColorHex.setTextColor(headerTextColor)
    selectedColorName.setTextColor(headerTextColor)
    selectedColorHex.text = selectedColor.toHex()

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

  private fun setupStatusBar() {
    title = ""
    setSupportActionBar(toolbar)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.generated_colors_menu, menu)
    val copyToClipboardItem = menu.findItem(R.id.copyToClipBoard)
    copyToClipboardItem.icon = ContextCompat.getDrawable(
      this, if (selectedColor().isDark()) {
        R.drawable.ic_content_copy_white_24dp
      } else {
        R.drawable.ic_content_copy_black_24dp
      }
    )
    this.menu = menu
    return true
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    menu.hideAction(R.id.copyToClipBoard)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.copyToClipBoard -> {
        val anchorView = findViewById<View>(R.id.copyToClipBoard)
        val popup = PopupMenu(this, anchorView)
        val options = resources.getStringArray(R.array.copy_modes)
        options.forEachIndexed { index, action ->
          popup.menu.add(0, index, index, action)
        }
        popup.setOnMenuItemClickListener {
          when (it.itemId) {
            0 -> copyToClipboard(selectedColor().toRGB())
            1 -> copyToClipboard(selectedColor().toHex())
            2 -> copyToClipboard(selectedColor().toCMYK())
            3 -> copyToClipboard(selectedColor().toHSL())
          }
          true
        }
        popup.show()
        true
      }
      else -> super.onOptionsItemSelected(item)
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
          showMenuOptions()
          animateHeaderTextAlpha()
          observeViewModelUpdates()
        }
      })
  }

  private fun showMenuOptions() {
    menu.showAction(R.id.copyToClipBoard)
  }

  private fun observeViewModelUpdates() {
    viewModel = ColorGenerationViewModel(selectedColor().toHexPureValue())
    viewModel.state.observe(this, Observer { state ->
      render(state)
    })
  }

  private fun render(nullableState: ScreenViewState?) {
    nullableState?.let { state ->
      when (state) {
        is ScreenViewState.Color -> {
          selectedColorName.text = resources.getString(R.string.color_name, state.colorName)
          selectedColorName.fadeIn()
        }
        is ScreenViewState.Error -> {
          selectedColorName.text = ""
          selectedColorName.fadeOut()
        }
      }
    }
  }

  private fun animateHeaderTextAlpha() {
    selectedColorHex.animate().alpha(1f).start()
  }

  private fun generateColors(selectedColor: Int) {
    generateComplimentary(selectedColor)
    generateShades(selectedColor)
    generateTints(selectedColor)
    generateAnalogous(selectedColor)
    generateTriadic(selectedColor)
    generateTetradic(selectedColor)
  }

  private fun generateShades(selectedColor: Int) {
    val adapter = GeneratedColorsAdapter(onColorClickListener())
    colorShadesList.adapter = adapter
    colorShadesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    colorShadesList.setHasFixedSize(true)
    adapter.colors = selectedColor.getShades().map { it.toColorDetails() }

    val hexColor = selectedColor.toHex()
    shadesTitle.text = resources.getString(R.string.shades, hexColor)
  }

  private fun generateTints(selectedColor: Int) {
    val adapter = GeneratedColorsAdapter(onColorClickListener())
    tintsList.adapter = adapter
    tintsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    tintsList.setHasFixedSize(true)
    adapter.colors = selectedColor.getTints().map { it.toColorDetails() }

    val hexColor = selectedColor.toHex()
    tintsTitle.text = resources.getString(R.string.tints, hexColor)
  }

  private fun generateComplimentary(selectedColor: Int) {
    val hexColor = selectedColor.toHex()
    complimentaryColorTitle.text = resources.getString(R.string.complimentary, hexColor)
    complimentaryColorBase.setBackgroundColor(selectedColor)
    complimentaryColor.setBackgroundColor(selectedColor.complimentary())

    complimentaryColor.setOnClickListener { launchWithNoTransition(this, selectedColor.complimentary()) }

    val hexColorComplimentary = selectedColor.complimentary().toHex()
    complimentaryColorBaseHex.text = hexColor
    complimentaryColorHex.text = hexColorComplimentary
  }

  private fun generateAnalogous(selectedColor: Int) {
    val hexColor = selectedColor.toHex()
    analogousColorsTitle.text = resources.getString(R.string.analogous, hexColor)
    analogousColorBase.setBackgroundColor(selectedColor)
    val analogousColors = selectedColor.analogous()
    analogousColor1.setBackgroundColor(analogousColors.first)
    analogousColor2.setBackgroundColor(analogousColors.second)

    analogousColor1.setOnClickListener { launchWithNoTransition(this, analogousColors.first) }
    analogousColor2.setOnClickListener { launchWithNoTransition(this, analogousColors.second) }

    analogousColorBaseHex.text = hexColor
    analogousColor1Hex.text = analogousColors.first.toHex()
    analogousColor2Hex.text = analogousColors.second.toHex()
  }

  private fun generateTriadic(selectedColor: Int) {
    val hexColor = selectedColor.toHex()
    triadicColorsTitle.text = resources.getString(R.string.triadic, hexColor)
    triadicColorBase.setBackgroundColor(selectedColor)
    val triadicColors = selectedColor.triadic()
    triadicColor1.setBackgroundColor(triadicColors.first)
    triadicColor2.setBackgroundColor(triadicColors.second)

    triadicColor1.setOnClickListener { launchWithNoTransition(this, triadicColors.first) }
    triadicColor2.setOnClickListener { launchWithNoTransition(this, triadicColors.second) }

    triadicColorBaseHex.text = hexColor
    triadicColor1Hex.text = triadicColors.first.toHex()
    triadicColor2Hex.text = triadicColors.second.toHex()
  }

  private fun generateTetradic(selectedColor: Int) {
    val hexColor = selectedColor.toHex()
    tetradicColorsTitle.text = resources.getString(R.string.tetradic, hexColor)
    tetradicColorBase.setBackgroundColor(selectedColor)
    val tetradicColors = selectedColor.tetradic()
    tetradicColor1.setBackgroundColor(tetradicColors.first)
    tetradicColor2.setBackgroundColor(tetradicColors.second)
    tetradicColor3.setBackgroundColor(tetradicColors.third)

    tetradicColor1.setOnClickListener { launchWithNoTransition(this, tetradicColors.first) }
    tetradicColor2.setOnClickListener { launchWithNoTransition(this, tetradicColors.second) }
    tetradicColor3.setOnClickListener { launchWithNoTransition(this, tetradicColors.third) }

    tetradicColorBaseHex.text = hexColor
    tetradicColor1Hex.text = tetradicColors.first.toHex()
    tetradicColor2Hex.text = tetradicColors.second.toHex()
    tetradicColor3Hex.text = tetradicColors.third.toHex()
  }

  private fun onColorClickListener(): (View, ColorViewState, Int) -> Unit = { _, colorDetails, _ ->
    launchWithNoTransition(this, colorDetails.color)
  }

  override fun onBackPressed() {
    if (selectedPosition() == -1) {
      backPressed()
    } else {
      menu.hideAction(R.id.copyToClipBoard)
      content.fadeOut()
      selectedColorHex.fadeOut()
      selectedColorName.fadeOut()
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
