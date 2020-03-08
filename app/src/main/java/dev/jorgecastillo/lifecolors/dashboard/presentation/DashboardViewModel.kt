package dev.jorgecastillo.lifecolors.dashboard.presentation

import androidx.lifecycle.viewModelScope
import dev.jorgecastillo.androidcolorx.library.HEXColor
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingRepository
import dev.jorgecastillo.lifecolors.colors.network.FirebaseColorsDatabase
import dev.jorgecastillo.lifecolors.common.presentation.BaseViewModel
import dev.jorgecastillo.lifecolors.common.presentation.ViewState
import dev.jorgecastillo.lifecolors.common.view.model.ColorType
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.zalandoclient.ZalandoApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ContentViewState(
    val clothes: List<ClothingItem>,
    val colors: List<ColorViewState>
)

@ExperimentalCoroutinesApi
class DashboardViewModel(
    private val clothingRepo: ClothingRepository = ClothingRepository(),
    private val colorsDatabase: FirebaseColorsDatabase = FirebaseColorsDatabase()
) : BaseViewModel<ContentViewState>() {

    fun onReadyToLoadContent(clothingCategory: ZalandoApiClient.ZalandoCategory) {
        viewModelScope.launch(Dispatchers.IO) {

            val clothesAndColorsFlow = clothingRepo.get(clothingCategory, HEXColor("#000000"))
                .combine(colorsDatabase.getFavedColors()) { clothes, colors ->
                    ContentViewState(clothes.take(6), colors.map {
                        ColorViewState(
                            it,
                            ColorType.GENERATED, isFavorite = true, isLoading = false
                        )
                    })
                }

            clothesAndColorsFlow.onEach { state ->
                    updateViewState { ViewState.Content(state) }
                }
                .catch {
                    updateViewState { ViewState.Error }
                }
                .collect()
        }
    }
}
