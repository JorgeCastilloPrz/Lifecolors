package dev.jorgecastillo.lifecolors.palettes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jorgecastillo.lifecolors.common.usecase.AreColorsFav
import dev.jorgecastillo.lifecolors.common.usecase.AreColorsFavResult
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.PICKED
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Colors
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Error
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Loading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.ArrayList

sealed class PalettesViewState {
  object Loading : PalettesViewState()
  object Error : PalettesViewState()
  data class Colors(val colors: List<ColorViewState>) : PalettesViewState()
}

fun Colors.hasNoPickedColors() = colors.none { it.type == PICKED }
fun Colors.pickedColors() = colors.filter { it.type == PICKED }
fun Colors.generatedColors() = colors.filter { it.type == GENERATED }

@Suppress("DeferredResultUnused")
class PalettesViewModel(
  private val areColorsFav: AreColorsFav = AreColorsFav(),
  private val toggleColorFav: ToggleColorFav = ToggleColorFav()
) : ViewModel() {

  private val _state: NonNullMutableLiveData<PalettesViewState> =
    NonNullMutableLiveData(Loading)

  val state: LiveData<PalettesViewState> = _state

  fun onGeneratedColorsAvailable(
    pickedColors: ArrayList<Int>,
    generatedColors: ArrayList<Int>
  ) {
    viewModelScope.async(Dispatchers.IO) {

      when (val colorsWithFavStatus = areColorsFav.execute(pickedColors + generatedColors)) {
        is AreColorsFavResult.Error -> {
        }
        is AreColorsFavResult.Success -> {
          updateViewState { state ->
            when (state) {
              Loading, Error -> Colors(colorsWithFavStatus.colorsWithFavStatus.toList().map {
                ColorViewState(
                  it.first,
                  if (pickedColors.contains(it.first)) PICKED else GENERATED,
                  it.second
                )
              })
              is Colors -> state.copy(colors = colorsWithFavStatus.colorsWithFavStatus.toList().map {
                ColorViewState(
                  it.first,
                  if (pickedColors.contains(it.first)) PICKED else GENERATED,
                  it.second
                )
              })
            }
          }
        }
      }
    }
  }

  private suspend fun updateViewState(transform: (PalettesViewState) -> PalettesViewState) {
    withContext(Main) {
      _state.value = transform(_state.value)
    }
  }
}
