package dev.jorgecastillo.lifecolors.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.camera.CameraActivity
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.lifecolors.clothingdetail.navigation.launchClothingItemDetail
import dev.jorgecastillo.lifecolors.colorgeneration.view.ClothesAdapter
import dev.jorgecastillo.lifecolors.colorgeneration.view.GeneratedColorsActivity
import dev.jorgecastillo.lifecolors.common.presentation.DashboardViewModelFactory
import dev.jorgecastillo.lifecolors.common.presentation.ViewState
import dev.jorgecastillo.lifecolors.common.view.AuthenticationActivity
import dev.jorgecastillo.lifecolors.common.view.extensions.hideKeyboard
import dev.jorgecastillo.lifecolors.dashboard.presentation.ContentViewState
import dev.jorgecastillo.lifecolors.dashboard.presentation.DashboardViewModel
import dev.jorgecastillo.lifecolors.favoritecolors.FavoriteColorsActivity
import dev.jorgecastillo.lifecolors.palettes.ColorsListAdapter
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.zalandoclient.ZalandoApiClient
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DashboardActivity : AuthenticationActivity() {

    private val viewModel by viewModels<DashboardViewModel> { DashboardViewModelFactory }

    private val savedClothesAdapter = ClothesAdapter(onClothingItemClick())
    private val colorsAdapter = ColorsListAdapter(onColorItemClick(), onColorFavClick())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setupAppBar()
        setupClothingSelector()
        setupClothingList()
        setupColorList()
        setupFab()

        viewModel.viewState.observe(this, Observer(::render))
        viewModel.onReadyToLoadContent(ZalandoApiClient.ZalandoCategory.Mujer.RopaMujer())
    }

    private fun setupAppBar() {
        setSupportActionBar(bar)
        bar.setNavigationOnClickListener { showSettingsPopup() }
    }

    private fun showSettingsPopup() {
        val popup = PopupMenu(this, anchorView)
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val options = resources.getStringArray(
            if (isLoggedIn) {
                R.array.main_menu_actions_logout
            } else {
                R.array.main_menu_actions_login
            }
        )
        options.forEachIndexed { index, action ->
            popup.menu.add(0, index, index, action)
        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                0 -> {
                }
                1 -> if (isLoggedIn) logout() else authenticate { }
            }
            true
        }
        popup.show()
    }

    private fun setupFab() {
        fab.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, CameraActivity::class.java))
        }
    }

    private fun setupClothingSelector() {
        val categories = ZalandoApiClient.ZalandoCategory.Mujer.all()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.item_category_dropdown,
            categories.map { resources.getString(it.stringId) }
        )

        clothingCategoryDropdown.setAdapter(adapter)
        clothingCategoryDropdown.setOnItemClickListener { _, _, pos, _ ->
            // viewModel.loadClothingSuggestions(categories[pos])
            clothingCategoryDropdown.hideKeyboard(this)
        }
    }

    private fun setupClothingList() {
        clothesRecycler.adapter = savedClothesAdapter
        clothesRecycler.layoutManager = GridLayoutManager(this, 3)
        clothesRecycler.setHasFixedSize(false)
    }

    private fun setupColorList() {
        pickedColorsList.adapter = colorsAdapter
        pickedColorsList.layoutManager = LinearLayoutManager(this)
        pickedColorsList.setHasFixedSize(false)
        val dividerDecorator = DividerItemDecoration(this).apply {
            setDrawable(
                ContextCompat.getDrawable(
                    this@DashboardActivity,
                    R.drawable.color_divider
                )!!
            )
        }
        pickedColorsList.addItemDecoration(dividerDecorator)
    }

    private fun onClothingItemClick(): (ClothingItem) -> Unit = { item ->
        launchClothingItemDetail(item)
    }

    private fun onColorItemClick(): (View, ColorViewState, Int) -> Unit = { view, details, _ ->
        window.enterTransition = null
        window.exitTransition = null
        GeneratedColorsActivity.launch(this, view, details.color)
    }

    private fun onColorFavClick(): (View, ColorViewState, Int) -> Unit = { view, state, position ->
        // viewModel.onColorFavClick(details, position)
    }

    private fun render(viewState: ViewState<ContentViewState>): Unit =
        when (viewState) {
            ViewState.Loading -> {
                loading.visibility = View.VISIBLE
                clothesRecycler.visibility = View.GONE
            }
            is ViewState.Content<*> -> {
                loading.visibility = View.GONE

                val content = viewState.t as ContentViewState
                clothingCategory.bindClothesDropdown(viewState.t)

                clothesRecycler.visibility = View.VISIBLE
                savedClothesAdapter.submitList(content.clothes)
                colorsAdapter.submitList(content.colors)

                if (content.colors.any { it.isPlaceHolder }) {
                    pickedColorsList.alpha = 0.2f
                } else {
                    pickedColorsList.alpha = 1f
                }

                viewAllClothes.bindViewAllClothesButtonText(viewState.t)
                viewAllColors.bindViewAllColorsButtonText(viewState.t)
            }
            ViewState.Error -> {
                loading.visibility = View.GONE
                showError()
            }
        }

    private fun TextInputLayout.bindClothesDropdown(state: ContentViewState) {
        isEnabled = !state.needsLogin || !state.clothes.any { it.isPlaceHolder }
    }

    private fun Button.bindViewAllClothesButtonText(state: ContentViewState) {
        setText(
            when {
                state.needsLogin -> {
                    R.string.view_all_clothes_login
                }
                state.clothes.any { it.isPlaceHolder } -> {
                    R.string.view_all_clothes_empty
                }
                else -> {
                    R.string.view_all
                }
            })

        setOnClickListener {
            when {
                state.needsLogin -> {
                    authenticate { }
                }
                state.colors.any { it.isPlaceHolder } -> {
                    R.string.view_all_colors_empty
                }
                else -> {
                    FavoriteColorsActivity.launch(this@DashboardActivity)
                }
            }
        }
    }

    private fun Button.bindViewAllColorsButtonText(state: ContentViewState) {
        setText(
            when {
                state.needsLogin -> {
                    R.string.view_all_colors_login
                }
                state.colors.any { it.isPlaceHolder } -> {
                    R.string.view_all_colors_empty
                }
                else -> {
                    R.string.view_all
                }
            })

        setOnClickListener {
            when {
                state.needsLogin -> {
                    authenticate { }
                }
                state.colors.any { it.isPlaceHolder } -> {
                    R.string.view_all_colors_empty
                }
                else -> {
                    FavoriteColorsActivity.launch(this@DashboardActivity)
                }
            }
        }
    }

    private fun showError() {
        Snackbar.make(main, R.string.error_server, Snackbar.LENGTH_LONG).show()
    }
}
