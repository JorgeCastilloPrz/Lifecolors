package dev.jorgecastillo.lifecolors.palettes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.jorgecastillo.lifecolors.common.usecase.AreColorsFav
import dev.jorgecastillo.lifecolors.common.usecase.AreColorsFavResult
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFavResult
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.PICKED
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Colors
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Error
import dev.jorgecastillo.lifecolors.palettes.toGeneratedColorDetails
import dev.jorgecastillo.lifecolors.palettes.toPickedColorDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.ArrayList

sealed class PalettesViewState {
  object Error : PalettesViewState()
  data class Colors(val colors: List<ColorViewState>) : PalettesViewState()
}

fun Colors.hasNoPickedColors() = colors.none { it.type == PICKED }
fun Colors.pickedColors() = colors.filter { it.type == PICKED }
fun Colors.generatedColors() = colors.filter { it.type == GENERATED }

@Suppress("DeferredResultUnused")
class PalettesViewModel(
  private val areColorsFav: AreColorsFav = AreColorsFav(),
  private val toggleColorFav: ToggleColorFav = ToggleColorFav(),
  private val pickedColors: ArrayList<Int>,
  private val generatedColors: ArrayList<Int>
) : ViewModel() {

  private val _state: NonNullMutableLiveData<PalettesViewState> =
    NonNullMutableLiveData(
      Colors(colors = pickedColors.toPickedColorDetails() + generatedColors.toGeneratedColorDetails())
    )

  val state: LiveData<PalettesViewState> = _state

  fun onColorsFavStateRequired() {
    if (FirebaseAuth.getInstance().currentUser != null) {
      updateViewState {
        when (it) {
          is Colors -> it.copy(colors = it.colors.map { color -> color.copy(isLoading = true) })
          else -> it
        }
      }

      viewModelScope.async(Dispatchers.IO) {
        when (val colorsWithFavStatus = areColorsFav.execute(pickedColors + generatedColors)) {
          is AreColorsFavResult.Error -> {
          }
          is AreColorsFavResult.Success -> {
            updateViewStateSuspend { state ->
              when (state) {
                Error -> Colors(colorsWithFavStatus.colorsWithFavStatus.toList().map {
                  ColorViewState(
                    it.first,
                    if (pickedColors.contains(it.first)) PICKED else GENERATED,
                    it.second,
                    false
                  )
                })
                is Colors -> state.copy(colors = colorsWithFavStatus.colorsWithFavStatus.toList().map {
                  ColorViewState(
                    it.first,
                    if (pickedColors.contains(it.first)) PICKED else GENERATED,
                    it.second,
                    false
                  )
                })
              }
            }
          }
        }
      }
    }
  }

  private fun updateViewState(transform: (PalettesViewState) -> PalettesViewState) {
    _state.value = transform(_state.value)
  }

  private suspend fun updateViewStateSuspend(transform: (PalettesViewState) -> PalettesViewState) {
    withContext(Main) {
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
        }
        is ToggleColorFavResult.Success -> {
          updateViewStateSuspend { state ->
            when (state) {
              Error -> Colors(
                listOf(
                  ColorViewState(
                    details.color,
                    details.type,
                    result.newFavState,
                    false
                  )
                )
              )
              is Colors -> state.copy(colors = state.colors.map { colorViewState ->
                ColorViewState(
                  colorViewState.color,
                  colorViewState.type,
                  if (state.colors.filter { it.type == details.type }.indexOf(colorViewState) == position) {
                    result.newFavState
                  } else {
                    colorViewState.isFavorite
                  },
                  false
                )
              })
            }
          }
        }
      }
    }
  }
}
