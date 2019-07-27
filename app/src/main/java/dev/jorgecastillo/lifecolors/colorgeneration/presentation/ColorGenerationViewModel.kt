package dev.jorgecastillo.lifecolors.colorgeneration.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import dev.jorgecastillo.lifecolors.colorgeneration.view.complimentary
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHex
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHexPureValue
import dev.jorgecastillo.lifecolors.common.domain.model.ColorDetails
import dev.jorgecastillo.lifecolors.common.usecase.IsColorFav
import dev.jorgecastillo.lifecolors.common.usecase.IsColorFavResult
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFavResult
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import dev.jorgecastillo.zalandoclient.ZalandoApiClient
import dev.jorgecastillo.zalandoclient.ZalandoApiClient.ZalandoCategory
import dev.jorgecastillo.zalandoclient.ZalandoApiClient.ZalandoCategory.Mujer.RopaMujer
import dev.jorgecastillo.zalandoclient.ZalandoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
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
  val suggestedClothes: List<ZalandoItem>,
  val suggestedComplimentaryClothes: List<ZalandoItem>
)

@Suppress("DeferredResultUnused")
class ColorGenerationViewModel(
  private val selectedColor: Int,
  private val isColorFav: IsColorFav = IsColorFav(),
  private val toggleColorFav: ToggleColorFav = ToggleColorFav(),
  private val zalandoApiClient: ZalandoApiClient = ZalandoApiClient()
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
            updateViewStateSuspend { it.copy(isShowingError = true, isLoadingFavState = false) }
          }
          is IsColorFavResult.Success -> {
            updateViewStateSuspend { it.copy(isFavorite = isColorFav.isFavorite, isLoadingFavState = false) }
          }
        }
      }
    }
  }

  private suspend fun updateViewStateSuspend(transform: (GeneratedColorsScreenViewState) -> GeneratedColorsScreenViewState) {
    withContext(Main) {
      _state.value = transform(_state.value)
    }
  }

  private fun updateViewState(transform: (GeneratedColorsScreenViewState) -> GeneratedColorsScreenViewState) {
    _state.value = transform(_state.value)
  }

  private fun loadColorDetails() {
    viewModelScope.async(Dispatchers.IO) {
      val client = OkHttpClient()
      val request = Request.Builder()
        .url("https://www.thecolorapi.com/id?hex=${selectedColor.toHexPureValue()}")
        .build()
      try {
        val response = client.newCall(request).execute()
        val body = response.body!!.string()
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(ColorDetails::class.java)
        val colorDetails = jsonAdapter.fromJson(body)!!

        updateViewStateSuspend { it.copy(colorName = colorDetails.name.value) }
        loadClothingSuggestions()
        loadComplimentaryClothingSuggestions()
      } catch (e: IOException) {
        updateViewStateSuspend { it.copy(isShowingError = true) }
        loadClothingSuggestions()
        loadComplimentaryClothingSuggestions()
      }
    }
  }

  fun loadClothingSuggestions(category: ZalandoCategory = RopaMujer()) {
    viewModelScope.async(Dispatchers.IO) {
      updateViewStateSuspend { it.copy(isLoadingSuggestedClothes = true) }

      val clothes = zalandoApiClient.get(category, selectedColor.toHex())
      updateViewStateSuspend { it.copy(isLoadingSuggestedClothes = false, suggestedClothes = clothes) }
    }
  }

  fun loadComplimentaryClothingSuggestions(category: ZalandoCategory = RopaMujer()) {
    viewModelScope.async(Dispatchers.IO) {
      updateViewStateSuspend { it.copy(isLoadingSuggestedComplimentaryClothes = true) }

      val clothes = zalandoApiClient.get(
        category,
        selectedColor.complimentary().toHex()
      )
      updateViewStateSuspend {
        it.copy(
          isLoadingSuggestedComplimentaryClothes = false,
          suggestedComplimentaryClothes = clothes
        )
      }
    }
  }

  fun onFavClick(color: Int) {
    updateViewState { it.copy(isLoadingFavState = true) }

    viewModelScope.async(Dispatchers.IO) {
      when (val result = toggleColorFav.execute(color)) {
        is ToggleColorFavResult.Error -> {
          updateViewStateSuspend { it.copy(isShowingError = true, isLoadingFavState = false) }
        }
        is ToggleColorFavResult.Success -> {
          updateViewStateSuspend { it.copy(isFavorite = result.newFavState, isLoadingFavState = false) }
        }
      }
    }
  }
}
