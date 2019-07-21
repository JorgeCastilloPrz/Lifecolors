package dev.jorgecastillo.lifecolors.favoritecolors

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.colorgeneration.view.GeneratedColorsActivity
import dev.jorgecastillo.lifecolors.common.view.AuthenticationActivity
import dev.jorgecastillo.lifecolors.fadeIn
import dev.jorgecastillo.lifecolors.favoritecolors.presentation.FavoriteColorsViewModel
import dev.jorgecastillo.lifecolors.favoritecolors.presentation.FavoriteColorsViewState
import dev.jorgecastillo.lifecolors.favoritecolors.presentation.FavoriteColorsViewState.Colors
import dev.jorgecastillo.lifecolors.favoritecolors.presentation.FavoriteColorsViewState.Loading
import dev.jorgecastillo.lifecolors.palettes.PaletteColorsAdapter
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import kotlinx.android.synthetic.main.activity_favorite_colors.colorList
import kotlinx.android.synthetic.main.activity_favorite_colors.loading
import kotlinx.android.synthetic.main.activity_favorite_colors.toolbar

class FavoriteColorsActivity : AuthenticationActivity() {

  companion object {
    fun launch(source: Activity) {
      source.startActivity(Intent(source, FavoriteColorsActivity::class.java))
    }
  }

  private lateinit var adapter: PaletteColorsAdapter
  private val viewModel: FavoriteColorsViewModel = FavoriteColorsViewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_favorite_colors)
    setupActionBar()
    setupColorList()
    animateStatusBarEnter()
  }

  private fun setupActionBar() {
    setSupportActionBar(toolbar)
  }

  private fun animateStatusBarEnter() {
    val transparentColor = Color.TRANSPARENT
    val accentColor = ContextCompat.getColor(this, R.color.colorAccent)
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), transparentColor, accentColor).apply {
      interpolator = window.sharedElementEnterTransition.interpolator
      addUpdateListener {
        window.statusBarColor = it.animatedValue as Int
      }
    }
    colorAnimation.start()
  }

  private fun setupColorList() {
    colorList.setHasFixedSize(true)
    colorList.layoutManager = LinearLayoutManager(this)
    adapter = PaletteColorsAdapter(colorClickListener(), favClickListener())
    colorList.adapter = adapter
    val dividerDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
      setDrawable(ContextCompat.getDrawable(this@FavoriteColorsActivity, R.drawable.color_divider)!!)
    }
    colorList.addItemDecoration(dividerDecorator)
  }

  private fun colorClickListener(): (View, ColorViewState, Int) -> Unit = { view, details, position ->
    window.enterTransition = null
    window.exitTransition = null
    GeneratedColorsActivity.launch(this, view, details.color, position)
  }

  private fun favClickListener(): (View, ColorViewState, Int) -> Unit = { view, details, position ->

  }

  override fun onResume() {
    super.onResume()
    viewModel.state.observe(this, Observer { state -> render(state) })
    viewModel.onScreenResumed()
  }

  private fun render(state: FavoriteColorsViewState) {
    when (state) {
      is Loading -> {
        loading.visibility = View.VISIBLE
      }
      is Error -> {
        loading.visibility = View.GONE
      }
      is Colors -> {
        loading.visibility = View.GONE
        adapter.colors = state.colors
        colorList.fadeIn()
      }
    }
  }
}
