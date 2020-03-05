package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jorgecastillo.androidcolorx.library.*
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.clothingdetail.navigation.launchClothingItemDetail
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ColorGenerationViewModel
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.GeneratedColorsScreenViewState
import dev.jorgecastillo.lifecolors.colorgeneration.view.list.GeneratedColorsAdapter
import dev.jorgecastillo.lifecolors.common.view.AuthenticationActivity
import dev.jorgecastillo.lifecolors.common.view.extensions.copyToClipboard
import dev.jorgecastillo.lifecolors.common.view.extensions.hideAction
import dev.jorgecastillo.lifecolors.common.view.extensions.hideKeyboard
import dev.jorgecastillo.lifecolors.common.view.extensions.showAction
import dev.jorgecastillo.lifecolors.common.view.list.ItemSnapHelper
import dev.jorgecastillo.lifecolors.common.view.menu.MenuItemProgressCircle
import dev.jorgecastillo.lifecolors.detail.view.BottomCutout.Companion.DEFAULT_COLOR
import dev.jorgecastillo.lifecolors.common.view.extensions.fadeIn
import dev.jorgecastillo.lifecolors.common.view.extensions.fadeOut
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.lifecolors.palettes.toColorDetails
import dev.jorgecastillo.lifecolors.camera.view.extensions.GUIUtils
import dev.jorgecastillo.lifecolors.camera.view.OnRevealAnimationListener
import dev.jorgecastillo.lifecolors.common.view.SimpleTransitionListener
import dev.jorgecastillo.zalandoclient.ZalandoApiClient.ZalandoCategory
import dev.jorgecastillo.zalandoclient.ZalandoItem
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
import kotlinx.android.synthetic.main.activity_generated_colors.suggestedClothesDropdown
import kotlinx.android.synthetic.main.activity_generated_colors.suggestedClothesList
import kotlinx.android.synthetic.main.activity_generated_colors.suggestedClothesLoader
import kotlinx.android.synthetic.main.activity_generated_colors.suggestedComplimentaryClothesDropdown
import kotlinx.android.synthetic.main.activity_generated_colors.suggestedComplimentaryClothesList
import kotlinx.android.synthetic.main.activity_generated_colors.suggestedComplimentaryClothesLoader
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

class GeneratedColorsActivity : AuthenticationActivity() {

  companion object {

    private const val PICKED_COLOR = "PICKED_COLORS"
    private const val OPEN_FROM_ITSELF = "OPEN_FROM_ITSELF"

    fun launchWithNoTransition(
      source: Activity,
      pickedColor: Int
    ) {
      val bundle = Bundle().apply {
        putInt(PICKED_COLOR, pickedColor)
        putBoolean(OPEN_FROM_ITSELF, true)
      }

      val intent = Intent(source, GeneratedColorsActivity::class.java)
      intent.putExtras(bundle)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
      source.startActivity(intent)
    }

    fun launch(
      source: Activity,
      sharedElement: View,
      pickedColor: Int
    ) {
      val bundle = Bundle().apply {
        putInt(PICKED_COLOR, pickedColor)
        putBoolean(OPEN_FROM_ITSELF, false)
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
  private val suggestedClothesAdapter = ClothesAdapter(onClothingItemClick())
  private val suggestedComplimentaryClothesAdapter = ClothesAdapter(onClothingItemClick())

  private fun selectedColor() = intent?.extras?.getInt(PICKED_COLOR, DEFAULT_COLOR) ?: DEFAULT_COLOR

  private fun openFromItself() = intent?.extras?.getBoolean(OPEN_FROM_ITSELF, false) ?: false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_generated_colors)
    postponeEnterTransition()
    setupStatusBar()
    setupLoaderColors()

    val selectedColor = selectedColor()
    val headerTextColor = selectedColor.contrasting()
    selectedColorHex.setTextColor(headerTextColor)
    selectedColorName.setTextColor(headerTextColor)
    selectedColorHex.text = selectedColor.asHex().toString()

    dot.color = selectedColor
    dot.transitionName = "$selectedColor"

    if (openFromItself()) {
      dot.strokeColor = Color.TRANSPARENT
      appBarLayout.post {
        animateRevealShow(selectedColor)
        animateStatusBarEnter()
        generateColors(selectedColor)
        setupSuggestedClothesLists()
      }
    } else {
      setupEnterAnimation(selectedColor)
      startPostponedEnterTransition()
      generateColors(selectedColor)
      setupSuggestedClothesLists()
    }
  }

  private fun setupLoaderColors() {
    val selectedColor = selectedColor()
    suggestedClothesLoader.setProgressBarTint(selectedColor)
    suggestedComplimentaryClothesLoader.setProgressBarTint(selectedColor)
  }

  private fun setupStatusBar() {
    title = ""
    setSupportActionBar(toolbar)
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    if (!::viewModel.isInitialized) {
      menu.hideAction(R.id.favColor)
      menu.hideAction(R.id.copyToClipBoard)
    }
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.generated_colors_menu, menu)
    val favIcon = menu.findItem(R.id.favColor)
    val copyToClipboardItem = menu.findItem(R.id.copyToClipBoard)
    renderMenuIcons(favIcon, copyToClipboardItem)

    this.menu = menu
    return true
  }

  private fun renderMenuIcons(favIcon: MenuItem, copyToClipboardItem: MenuItem) {
    if (selectedColor().isDark()) {
      renderClipboardIcon(copyToClipboardItem, R.drawable.ic_content_copy_white_24dp)
      renderFavIcon(
        favIcon,
        R.drawable.ic_favorite_white_24dp,
        R.drawable.ic_favorite_border_white_24dp,
        MenuItemProgressCircle(this).apply {
          this.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
        })
    } else {
      renderClipboardIcon(copyToClipboardItem, R.drawable.ic_content_copy_black_24dp)
      renderFavIcon(
        favIcon,
        R.drawable.ic_favorite_black_24dp,
        R.drawable.ic_favorite_border_black_24dp,
        MenuItemProgressCircle(this).apply {
          this.indeterminateTintList = ColorStateList.valueOf(Color.BLACK)
        })
    }
  }

  private fun renderClipboardIcon(clipboardItem: MenuItem, @DrawableRes iconRes: Int) {
    clipboardItem.icon = ContextCompat.getDrawable(this, iconRes)
  }

  @Suppress("SENSELESS_COMPARISON")
  private fun renderFavIcon(
    favIcon: MenuItem,
    @DrawableRes favedIconRes: Int,
    @DrawableRes unfavedIconRes: Int,
    loadingProgressBar: ProgressBar
  ) {
    if (::viewModel.isInitialized) {
      if (viewModel.state.value!!.isLoadingFavState) {
        favIcon.actionView = loadingProgressBar
      } else {
        favIcon.actionView = null
        val state = viewModel.state.value!!
        favIcon.icon = ContextCompat.getDrawable(this, if (state.isFavorite) favedIconRes else unfavedIconRes)
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.favColor -> {
        authenticate(
          onAuthenticationSuccess = { viewModel.onFavClick(selectedColor()) }
        )
        true
      }
      R.id.copyToClipBoard -> {
        val anchorView = findViewById<View>(R.id.copyToClipBoard)
        val popup = PopupMenu(this, anchorView)
        val options = resources.getStringArray(R.array.copy_modes)
        options.forEachIndexed { index, action ->
          popup.menu.add(0, index, index, action)
        }
        popup.setOnMenuItemClickListener {
          when (it.itemId) {
            0 -> copyToClipboard(selectedColor().asRgb().toString())
            1 -> copyToClipboard(selectedColor().asHex().toString())
            2 -> copyToClipboard(selectedColor().asCmyk().toString())
            3 -> copyToClipboard(selectedColor().asHsl().toString())
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
      cx, cy, object :
        OnRevealAnimationListener {
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
    menu.showAction(R.id.favColor)
    menu.showAction(R.id.copyToClipBoard)
  }

  private fun observeViewModelUpdates() {
    viewModel = ColorGenerationViewModel(selectedColor())
    viewModel.state.observe(this, Observer { state ->
      render(state)
    })
  }

  private fun render(state: GeneratedColorsScreenViewState) {
    when {
      state.isShowingError -> {
        selectedColorName.text = ""
        selectedColorName.fadeOut()
      }
      else -> {
        invalidateOptionsMenu()
        if (state.colorName.isEmpty()) {
          selectedColorName.text = ""
        } else {
          selectedColorName.text = resources.getString(R.string.color_name, state.colorName)
        }
        selectedColorName.fadeIn()
        renderClothesLists(state)
      }
    }
  }

  private fun renderClothesLists(state: GeneratedColorsScreenViewState) {
    if (state.isLoadingSuggestedClothes) {
      suggestedClothesLoader.visibility = View.VISIBLE
      suggestedClothesList.visibility = View.GONE
    } else {
      suggestedClothesLoader.visibility = View.GONE
      suggestedClothesList.visibility = View.VISIBLE
      suggestedClothesAdapter.submitList(state.suggestedClothes)
    }

    if (state.isLoadingSuggestedComplimentaryClothes) {
      suggestedComplimentaryClothesLoader.visibility = View.VISIBLE
      suggestedComplimentaryClothesList.visibility = View.GONE
    } else {
      suggestedComplimentaryClothesLoader.visibility = View.GONE
      suggestedComplimentaryClothesList.visibility = View.VISIBLE
      suggestedComplimentaryClothesAdapter.submitList(state.suggestedComplimentaryClothes)
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
    adapter.colors = selectedColor.shades().map { it.toColorDetails() }
    shadesTitle.text = resources.getString(R.string.shades)
  }

  private fun generateTints(selectedColor: Int) {
    val adapter = GeneratedColorsAdapter(onColorClickListener())
    tintsList.adapter = adapter
    tintsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    tintsList.setHasFixedSize(true)
    adapter.colors = selectedColor.tints().map { it.toColorDetails() }
    tintsTitle.text = resources.getString(R.string.tints)
  }

  private fun generateComplimentary(selectedColor: Int) {
    val hexColor = selectedColor.asHex()
    complimentaryColorTitle.text = resources.getString(R.string.complimentary)
    complimentaryColorBase.setColor(selectedColor)
    complimentaryColor.setColor(selectedColor.complimentary())

    complimentaryColor.setOnClickListener { launchWithNoTransition(this, selectedColor.complimentary()) }

    val hexColorComplimentary = selectedColor.complimentary().asHex()
    complimentaryColorBaseHex.text = hexColor.toString()
    complimentaryColorHex.text = hexColorComplimentary.toString()
  }

  private fun generateAnalogous(selectedColor: Int) {
    val hexColor = selectedColor.asHex()
    analogousColorsTitle.text = resources.getString(R.string.analogous)
    analogousColorBase.setColor(selectedColor)
    val analogousColors = selectedColor.analogous()
    analogousColor1.setColor(analogousColors.first)
    analogousColor2.setColor(analogousColors.second)

    analogousColor1.setOnClickListener { launchWithNoTransition(this, analogousColors.first) }
    analogousColor2.setOnClickListener { launchWithNoTransition(this, analogousColors.second) }

    analogousColorBaseHex.text = hexColor.toString()
    analogousColor1Hex.text = analogousColors.first.asHex().toString()
    analogousColor2Hex.text = analogousColors.second.asHex().toString()
  }

  private fun generateTriadic(selectedColor: Int) {
    val hexColor = selectedColor.asHex()
    triadicColorsTitle.text = resources.getString(R.string.triadic)
    triadicColorBase.setColor(selectedColor)
    val triadicColors = selectedColor.triadic()
    triadicColor1.setColor(triadicColors.first)
    triadicColor2.setColor(triadicColors.second)

    triadicColor1.setOnClickListener { launchWithNoTransition(this, triadicColors.first) }
    triadicColor2.setOnClickListener { launchWithNoTransition(this, triadicColors.second) }

    triadicColorBaseHex.text = hexColor.toString()
    triadicColor1Hex.text = triadicColors.first.asHex().toString()
    triadicColor2Hex.text = triadicColors.second.asHex().toString()
  }

  private fun generateTetradic(selectedColor: Int) {
    val hexColor = selectedColor.asHex()
    tetradicColorsTitle.text = resources.getString(R.string.tetradic)
    tetradicColorBase.setColor(selectedColor)
    val tetradicColors = selectedColor.tetradic()
    tetradicColor1.setColor(tetradicColors.first)
    tetradicColor2.setColor(tetradicColors.second)
    tetradicColor3.setColor(tetradicColors.third)

    tetradicColor1.setOnClickListener { launchWithNoTransition(this, tetradicColors.first) }
    tetradicColor2.setOnClickListener { launchWithNoTransition(this, tetradicColors.second) }
    tetradicColor3.setOnClickListener { launchWithNoTransition(this, tetradicColors.third) }

    tetradicColorBaseHex.text = hexColor.toString()
    tetradicColor1Hex.text = tetradicColors.first.asHex().toString()
    tetradicColor2Hex.text = tetradicColors.second.asHex().toString()
    tetradicColor3Hex.text = tetradicColors.third.asHex().toString()
  }

  private fun setupSuggestedClothesLists() {
    setupSuggestedClothes()
    setupSuggestedComplimentaryClothes()
  }

  private fun setupSuggestedClothes() {
    val categories = ZalandoCategory.Mujer.all()
    val adapter = ArrayAdapter(this, R.layout.item_dropdown, categories.map { resources.getString(it.stringId) })
    suggestedClothesDropdown.setAdapter(adapter)
    suggestedClothesDropdown.setOnItemClickListener { _, _, pos, _ ->
      viewModel.loadClothingSuggestions(categories[pos])
      suggestedClothesDropdown.hideKeyboard(this)
    }

    suggestedClothesList.adapter = suggestedClothesAdapter
    suggestedClothesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    suggestedClothesList.setHasFixedSize(true)
    ItemSnapHelper().attachToRecyclerView(suggestedClothesList)
  }

  private fun setupSuggestedComplimentaryClothes() {
    val categories = ZalandoCategory.Mujer.all()
    val adapter = ArrayAdapter(this, R.layout.item_dropdown, categories.map { resources.getString(it.stringId) })
    suggestedComplimentaryClothesDropdown.setAdapter(adapter)
    suggestedComplimentaryClothesDropdown.setOnItemClickListener { _, _, pos, _ ->
      viewModel.loadComplimentaryClothingSuggestions(categories[pos])
      suggestedComplimentaryClothesDropdown.hideKeyboard(this)
    }

    suggestedComplimentaryClothesList.adapter = suggestedComplimentaryClothesAdapter
    suggestedComplimentaryClothesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    suggestedComplimentaryClothesList.setHasFixedSize(true)
    ItemSnapHelper().attachToRecyclerView(suggestedComplimentaryClothesList)
  }

  private fun onColorClickListener(): (View, ColorViewState, Int) -> Unit = { _, colorDetails, _ ->
    launchWithNoTransition(this, colorDetails.color)
  }

  private fun onClothingItemClick(): (ZalandoItem) -> Unit = { item ->
    launchClothingItemDetail(item)
  }

  override fun onBackPressed() {
    if (openFromItself()) {
      backPressed()
    } else {
      menu.hideAction(R.id.favColor)
      menu.hideAction(R.id.copyToClipBoard)
      content.fadeOut()
      selectedColorHex.fadeOut()
      selectedColorName.fadeOut()
      GUIUtils.animateRevealHide(
        appBarLayout,
        selectedColor(),
        ContextCompat.getColor(this, R.color.background),
        dot.width / 2,
        object :
          OnRevealAnimationListener {
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
