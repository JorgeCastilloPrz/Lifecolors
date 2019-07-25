package dev.jorgecastillo.lifecolors.favoritecolors.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jorgecastillo.lifecolors.common.usecase.GetFavColors
import dev.jorgecastillo.lifecolors.common.usecase.GetFavColorsResult
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFavResult
import dev.jorgecastillo.lifecolors.common.usecase.execute
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED
import dev.jorgecastillo.lifecolors.favoritecolors.presentation.FavoriteColorsViewState.Colors
import dev.jorgecastillo.lifecolors.favoritecolors.presentation.FavoriteColorsViewState.Error
import dev.jorgecastillo.lifecolors.favoritecolors.presentation.FavoriteColorsViewState.Loading
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

sealed class FavoriteColorsViewState {
  object Loading : FavoriteColorsViewState()
  object Error : FavoriteColorsViewState()
  data class Colors(val colors: List<ColorViewState>) : FavoriteColorsViewState()
}

@Suppress("DeferredResultUnused")
class FavoriteColorsViewModel(
  private val getFavColors: GetFavColors = GetFavColors(),
  private val toggleColorFav: ToggleColorFav = ToggleColorFav()
) : ViewModel() {

  private val _state: NonNullMutableLiveData<FavoriteColorsViewState> =
    NonNullMutableLiveData(Loading)

  val state: LiveData<FavoriteColorsViewState> = _state

  fun onScreenResumed() {
    viewModelScope.async(Dispatchers.IO) {
      when (val favColors = getFavColors.execute()) {
        is GetFavColorsResult.Error -> {
        }
        is GetFavColorsResult.Success -> {
          updateViewState { state ->
            when (state) {
              Loading, Error -> Colors(favColors.colors.toList().map {
                ColorViewState(it, GENERATED, true)
              })
              is Colors -> state.copy(colors = favColors.colors.toList().map {
                ColorViewState(it, GENERATED, true)
              })
            }
          }
        }
      }
    }
  }

  private suspend fun updateViewState(transform: (FavoriteColorsViewState) -> FavoriteColorsViewState) {
    withContext(Dispatchers.Main) {
      _state.value = transform(_state.value)
    }
  }

  fun onColorFavClick(details: ColorViewState, position: Int) {
    viewModelScope.async(Dispatchers.IO) {
      when (val result = toggleColorFav.execute(details.color)) {
        is ToggleColorFavResult.Error -> {
        }
        is ToggleColorFavResult.Success -> {
          updateViewState { state ->
            when (state) {
              Loading, Error -> Colors(
                listOf(
                  ColorViewState(
                    details.color,
                    details.type,
                    result.newFavState
                  )
                )
              )
              is Colors -> state.copy(colors = state.colors.foldIndexed(listOf()) { pos, acc, item ->
                if (pos != position) {
                  acc + item
                } else {
                  acc
                }
              })
            }
          }
        }
      }
    }
  }
}
