package dev.jorgecastillo.lifecolors.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.camera.CameraActivity
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.lifecolors.clothingdetail.navigation.launchClothingItemDetail
import dev.jorgecastillo.lifecolors.colorgeneration.view.ClothesAdapter
import dev.jorgecastillo.lifecolors.common.view.AuthenticationActivity
import dev.jorgecastillo.lifecolors.common.view.extensions.hideKeyboard
import dev.jorgecastillo.lifecolors.common.view.list.ItemSnapHelper
import dev.jorgecastillo.zalandoclient.ZalandoApiClient
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : AuthenticationActivity() {

    private val savedClothesAdapter = ClothesAdapter(onClothingItemClick())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setupClothingSelector()
        setupClothingList()
        setupFab()
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
        clothesRecycler.layoutManager = GridLayoutManager(this, 2)
        clothesRecycler.setHasFixedSize(true)
        ItemSnapHelper().attachToRecyclerView(clothesRecycler)
    }

    private fun onClothingItemClick(): (ClothingItem) -> Unit = { item ->
        launchClothingItemDetail(item)
    }
}
