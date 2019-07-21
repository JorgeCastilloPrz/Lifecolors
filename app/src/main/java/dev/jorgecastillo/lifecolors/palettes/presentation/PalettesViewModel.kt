package dev.jorgecastillo.lifecolors.palettes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.jorgecastillo.lifecolors.common.usecase.AreColorsFav
import dev.jorgecastillo.lifecolors.common.usecase.ToggleColorFav
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.PICKED
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Colors
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Error
import dev.jorgecastillo.lifecolors.palettes.presentation.PalettesViewState.Idle
import java.util.ArrayList

sealed class PalettesViewState {
  object Idle : PalettesViewState()
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
    NonNullMutableLiveData(Idle)

  val state: LiveData<PalettesViewState> = _state

  fun onGeneratedColorsAvailable(
    pickedColors: ArrayList<Int>,
    generatedColors: ArrayList<Int>
  ) {
    areColorsFav.execute(
      pickedColors + generatedColors,
      onFailure = {},
      onSuccess = { result ->
        updateViewState { state ->
          when (state) {
            Idle, Error -> Colors(result.toList().map {
              ColorViewState(
                it.first,
                if (pickedColors.contains(it.first)) PICKED else GENERATED,
                it.second
              )
            })
            is Colors -> state.copy(colors = result.toList().map {
              ColorViewState(
                it.first,
                if (pickedColors.contains(it.first)) PICKED else GENERATED,
                it.second
              )
            })
          }
        }
      })
  }

  private fun updateViewState(transform: (PalettesViewState) -> PalettesViewState) {
    _state.postValue(transform(_state.value))
  }
}
