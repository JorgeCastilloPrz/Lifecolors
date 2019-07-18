package dev.jorgecastillo.lifecolors.colorgeneration.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import dev.jorgecastillo.lifecolors.common.domain.model.ColorDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

sealed class ScreenViewState {
  data class Color(val colorName: String) : ScreenViewState()
  object Error : ScreenViewState()
}

class ColorGenerationViewModel(private val selectedColorHex: String) : ViewModel() {

  private val _state: MutableLiveData<ScreenViewState> =
    MutableLiveData<ScreenViewState>().apply { value = ScreenViewState.Error }

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
          _state.value = ScreenViewState.Color(colorDetails!!.name.value)
        }
      } catch (e: IOException) {
        _state.value = ScreenViewState.Error
      }
    }
  }
}
