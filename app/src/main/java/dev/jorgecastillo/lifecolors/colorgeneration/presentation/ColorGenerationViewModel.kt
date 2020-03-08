package dev.jorgecastillo.lifecolors.colorgeneration.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import dev.jorgecastillo.androidcolorx.library.asHex
import dev.jorgecastillo.androidcolorx.library.complimentary
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingRepository
import dev.jorgecastillo.lifecolors.common.domain.model.ColorDetails
import dev.jorgecastillo.lifecolors.common.usecase.IsColorFav
import dev.jorgecastillo.lifecolors.common.usecase.IsColorFavResult
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFavResult
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import dev.jorgecastillo.zalandoclient.ZalandoApiClient.ZalandoCategory
import dev.jorgecastillo.zalandoclient.ZalandoApiClient.ZalandoCategory.Mujer.RopaMujer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class GeneratedColorsScreenViewState(
    val colorName: String,
    val isFavorite: Boolean,
    val isLoadingFavState: Boolean,
    val isShowingError: Boolean,
    val isLoadingSuggestedClothes: Boolean,
    val isLoadingSuggestedComplimentaryClothes: Boolean,
    val suggestedClothes: List<ClothingItem>,
    val suggestedComplimentaryClothes: List<ClothingItem>
)

@ExperimentalCoroutinesApi
@Suppress("DeferredResultUnused")
class ColorGenerationViewModel(
    private val selectedColor: Int,
    private val isColorFav: IsColorFav = IsColorFav(),
    private val toggleColorFav: ToggleColorFav = ToggleColorFav(),
    private val clothingRepository: ClothingRepository = ClothingRepository()
) : ViewModel() {

    private val _state: NonNullMutableLiveData<GeneratedColorsScreenViewState> =
        NonNullMutableLiveData(
            GeneratedColorsScreenViewState(
                "",
                isFavorite = false,
                isLoadingFavState = false,
                isShowingError = false,
                isLoadingSuggestedClothes = true,
                isLoadingSuggestedComplimentaryClothes = true,
                suggestedClothes = listOf(),
                suggestedComplimentaryClothes = listOf()
            )
        )

    val state: LiveData<GeneratedColorsScreenViewState> = _state

    init {
        loadColorFavState()
        loadColorDetails()
    }

    private fun loadColorFavState() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            updateViewState { it.copy(isLoadingFavState = true) }

            viewModelScope.async(Dispatchers.IO) {
                when (val isColorFav = isColorFav.execute(selectedColor)) {
                    is IsColorFavResult.Error -> {
                        updateViewState {
                            it.copy(
                                isShowingError = true,
                                isLoadingFavState = false
                            )
                        }
                    }
                    is IsColorFavResult.Success -> {
                        updateViewState {
                            it.copy(
                                isFavorite = isColorFav.isFavorite,
                                isLoadingFavState = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateViewState(transform: (GeneratedColorsScreenViewState) -> GeneratedColorsScreenViewState) {
        _state.postValue(transform(_state.value))
    }

    private fun loadColorDetails() {
        viewModelScope.async(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(
                    "https://www.thecolorapi.com/id?hex=${selectedColor.asHex().toString().drop(1)}"
                )
                .build()
            try {
                val response = client.newCall(request).execute()
                val body = response.body!!.string()
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(ColorDetails::class.java)
                val colorDetails = jsonAdapter.fromJson(body)!!

                updateViewState { it.copy(colorName = colorDetails.name.value) }
                loadClothingSuggestions()
                loadComplimentaryClothingSuggestions()
            } catch (e: IOException) {
                updateViewState { it.copy(isShowingError = true) }
                loadClothingSuggestions()
                loadComplimentaryClothingSuggestions()
            }
        }
    }

    fun loadClothingSuggestions(category: ZalandoCategory = RopaMujer()) {
        updateViewState { it.copy(isLoadingSuggestedClothes = true) }

        viewModelScope.launch(Dispatchers.IO) {
            clothingRepository.get(category, selectedColor.asHex())
                .onEach { clothes ->
                    updateViewState {
                        it.copy(
                            isLoadingSuggestedClothes = false,
                            suggestedClothes = clothes.take(6)
                        )
                    }
                }
                .catch { error ->
                    error
                    updateViewState { it.copy(isShowingError = true) }
                }
                .collect()
        }
    }

    fun loadComplimentaryClothingSuggestions(category: ZalandoCategory = RopaMujer()) {
        updateViewState { it.copy(isLoadingSuggestedComplimentaryClothes = true) }

        viewModelScope.launch(Dispatchers.IO) {
            clothingRepository.get(category, selectedColor.complimentary().asHex())
                .onEach { clothes ->
                    updateViewState {
                        it.copy(
                            isLoadingSuggestedComplimentaryClothes = false,
                            suggestedComplimentaryClothes = clothes.take(6)
                        )
                    }
                }
                .catch { updateViewState { it.copy(isShowingError = true) } }
                .collect()
        }
    }

    fun onFavClick(color: Int) {
        updateViewState { it.copy(isLoadingFavState = true) }

        viewModelScope.async(Dispatchers.IO) {
            when (val result = toggleColorFav.execute(color)) {
                is ToggleColorFavResult.Error -> {
                    updateViewState {
                        it.copy(
                            isShowingError = true,
                            isLoadingFavState = false
                        )
                    }
                }
                is ToggleColorFavResult.Success -> {
                    updateViewState {
                        it.copy(
                            isFavorite = result.newFavState,
                            isLoadingFavState = false
                        )
                    }
                }
            }
        }
    }

    fun onClothingItemFav(item: ClothingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                clothingRepository.toggleItemFav(item)
            } catch (e: Exception) {
                // updateViewState { it.copy(isShowingError = true) }
            }
        }
    }
}
