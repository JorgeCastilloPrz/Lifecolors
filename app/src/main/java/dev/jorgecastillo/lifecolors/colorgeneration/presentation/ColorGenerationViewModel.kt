package dev.jorgecastillo.lifecolors.colorgeneration.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ScreenViewState.Color
import dev.jorgecastillo.lifecolors.colorgeneration.presentation.ScreenViewState.Error
import dev.jorgecastillo.lifecolors.common.domain.model.ColorDetails
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

sealed class ScreenViewState {
  data class Color(val colorName: String, val isFavorite: Boolean) : ScreenViewState()
  object Error : ScreenViewState()
}

@Suppress("DeferredResultUnused")
class ColorGenerationViewModel(
  private val selectedColorHex: String,
  private val toggleColorFav: ToggleColorFav = ToggleColorFav()
) : ViewModel() {

  private val _state: NonNullMutableLiveData<ScreenViewState> =
    NonNullMutableLiveData(Error)

  val state: LiveData<ScreenViewState> = _state

  init {
    loadColorDetails()
  }

  private fun loadColorDetails() {
    viewModelScope.async(Dispatchers.IO) {
      val client = OkHttpClient()
      val request = Request.Builder()
        .url("https://www.thecolorapi.com/id?hex=$selectedColorHex")
        .build()
      try {
        val response = client.newCall(request).execute()
        val body = response.body!!.string()
        withContext(Dispatchers.Main) {
          val moshi = Moshi.Builder().build()
          val jsonAdapter = moshi.adapter(ColorDetails::class.java)
          val colorDetails = jsonAdapter.fromJson(body)
          _state.value = when (val state = _state.value) {
            is Color -> state.copy(colorName = colorDetails!!.name.value)
            Error -> Error
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
      onSuccess = { res ->
        res
      })
  }
}
