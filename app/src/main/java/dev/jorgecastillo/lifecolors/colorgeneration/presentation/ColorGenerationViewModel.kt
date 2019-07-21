package dev.jorgecastillo.lifecolors.colorgeneration.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ColorGenerationViewState.Color
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ColorGenerationViewState.Error
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ColorGenerationViewState.Idle
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHex
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHexPureValue
import dev.jorgecastillo.lifecolors.common.domain.model.ColorDetails
import dev.jorgecastillo.lifecolors.common.usecase.IsColorFav
import dev.jorgecastillo.lifecolors.common.usecase.IsColorFavResult
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFavResult
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

sealed class ColorGenerationViewState {
  object Idle : ColorGenerationViewState()
  object Error : ColorGenerationViewState()
  data class Color(val colorName: String, val isFavorite: Boolean) : ColorGenerationViewState()
}

@Suppress("DeferredResultUnused")
class ColorGenerationViewModel(
  private val selectedColor: Int,
  private val isColorFav: IsColorFav = IsColorFav(),
  private val toggleColorFav: ToggleColorFav = ToggleColorFav()
) : ViewModel() {

  private val _state: NonNullMutableLiveData<ColorGenerationViewState> =
    NonNullMutableLiveData(Idle)

  val state: LiveData<ColorGenerationViewState> = _state

  init {
    loadColorFavState()
    loadColorDetails()
  }

  private fun loadColorFavState() {
    viewModelScope.async(Dispatchers.IO) {
      when (val isColorFav = isColorFav.execute(selectedColor)) {
        is IsColorFavResult.Error -> {
        }
        is IsColorFavResult.Success -> {
          updateViewState { state ->
            when (state) {
              Idle -> Color("", isColorFav.isFavorite)
              Error -> Color("", isColorFav.isFavorite)
              is Color -> state.copy(isFavorite = isColorFav.isFavorite)
            }
          }
        }
      }
    }
  }

  private suspend fun updateViewState(transform: (ColorGenerationViewState) -> ColorGenerationViewState) {
    withContext(Main) {
      _state.value = transform(_state.value)
    }
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
        withContext(Main) {
          val moshi = Moshi.Builder().build()
          val jsonAdapter = moshi.adapter(ColorDetails::class.java)
          val colorDetails = jsonAdapter.fromJson(body)
          updateViewState { state ->
            when (state) {
              Idle -> Color(colorName = colorDetails!!.name.value, isFavorite = false)
              Error -> Color(colorName = colorDetails!!.name.value, isFavorite = false)
              is Color -> state.copy(colorName = colorDetails!!.name.value)
            }
          }
        }
      } catch (e: IOException) {
        _state.value = Error
      }
    }
  }

  fun onFavClick(color: Int) {
    viewModelScope.async(Dispatchers.IO) {
      when (val result = toggleColorFav.execute(color)) {
        is ToggleColorFavResult.Error -> {
        }
        is ToggleColorFavResult.Success -> {
          updateViewState { state ->
            when (state) {
              Idle -> Color(color.toHex(), result.newFavState)
              Error -> Color(color.toHex(), result.newFavState)
              is Color -> state.copy(isFavorite = result.newFavState)
            }
          }
        }
      }
    }
  }
}
