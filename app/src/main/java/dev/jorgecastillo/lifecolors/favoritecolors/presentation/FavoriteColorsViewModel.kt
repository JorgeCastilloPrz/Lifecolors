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
          updateViewStateSuspend { state ->
            when (state) {
              Loading, Error -> Colors(favColors.colors.toList().map {
                ColorViewState(it, GENERATED, isFavorite = true, isLoading = false)
              })
              is Colors -> state.copy(colors = favColors.colors.toList().map {
                ColorViewState(it, GENERATED, isFavorite = true, isLoading = false)
              })
            }
          }
        }
      }
    }
  }

  private fun updateViewState(transform: (FavoriteColorsViewState) -> FavoriteColorsViewState) {
    _state.value = transform(_state.value)
  }

  private suspend fun updateViewStateSuspend(transform: (FavoriteColorsViewState) -> FavoriteColorsViewState) {
    withContext(Dispatchers.Main) {
      _state.value = transform(_state.value)
    }
  }

  fun onColorFavClick(details: ColorViewState, position: Int) {
    updateViewState {
      (it as Colors).let { viewState ->
        viewState.copy(colors = it.colors.map { color ->
          if (color == details) {
            color.copy(isLoading = true)
          } else color
        })
      }
    }

    viewModelScope.async(Dispatchers.IO) {
      when (val result = toggleColorFav.execute(details.color)) {
        is ToggleColorFavResult.Error -> {
          updateViewState {
            (it as Colors).let { viewState ->
              viewState.copy(colors = it.colors.map { color -> color.copy(isLoading = false) })
            }
          }
        }
        is ToggleColorFavResult.Success -> {
          updateViewStateSuspend { state ->
            when (state) {
              Loading, Error -> Colors(
                listOf(
                  ColorViewState(
                    details.color,
                    details.type,
                    result.newFavState,
                    false
                  )
                )
              )
              is Colors -> state.copy(colors = state.colors.foldIndexed(listOf()) { pos, acc, item ->
                if (pos != position) {
                  acc + item.copy(isLoading = false)
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
