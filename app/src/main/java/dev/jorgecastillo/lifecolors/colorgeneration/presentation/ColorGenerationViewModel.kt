package dev.jorgecastillo.lifecolors.colorgeneration.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ScreenViewState.Color
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ScreenViewState.Error
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ScreenViewState.Idle
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHex
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHexPureValue
import dev.jorgecastillo.lifecolors.common.domain.model.ColorDetails
import dev.jorgecastillo.lifecolors.common.usecase.IsColorFav
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

sealed class ScreenViewState {
  object Idle : ScreenViewState()
  object Error : ScreenViewState()
  data class Color(val colorName: String, val isFavorite: Boolean) : ScreenViewState()
}

@Suppress("DeferredResultUnused")
class ColorGenerationViewModel(
  private val selectedColor: Int,
  private val isColorFav: IsColorFav = IsColorFav(),
  private val toggleColorFav: ToggleColorFav = ToggleColorFav()
) : ViewModel() {

  private val _state: NonNullMutableLiveData<ScreenViewState> =
    NonNullMutableLiveData(Idle)

  val state: LiveData<ScreenViewState> = _state

  init {
    loadColorFavState()
    loadColorDetails()
  }

  private fun loadColorFavState() {
    isColorFav.execute(
      selectedColor,
      onFailure = {},
      onSuccess = { newValue ->
        updateViewState { state ->
          when (state) {
            Idle -> Color("", newValue)
            Error -> Color("", newValue)
            is Color -> state.copy(isFavorite = newValue)
          }
        }
      })
  }

  private fun updateViewState(transform: (ScreenViewState) -> ScreenViewState) {
    _state.postValue(transform(_state.value))
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
        withContext(Dispatchers.Main) {
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
    toggleColorFav.execute(
      color,
      onFailure = {},
      onSuccess = { newValue ->
        updateViewState { state ->
          when (state) {
            Idle -> Color(color.toHex(), newValue)
            Error -> Color(color.toHex(), newValue)
            is Color -> state.copy(isFavorite = newValue)
          }
        }
      })
  }
}
