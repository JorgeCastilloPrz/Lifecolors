package dev.jorgecastillo.lifecolors.palettes.domain.model

import androidx.annotation.ColorInt
import dev.jorgecastillo.androidcolorx.library.asHex
import dev.jorgecastillo.lifecolors.common.view.model.ColorType
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED

data class ColorViewState(
  @ColorInt val color: Int,
  val type: ColorType,
  val isFavorite: Boolean,
  val isLoading: Boolean,
  val isPlaceHolder: Boolean = false
) {

  override fun toString(): String {
    return "${super.toString()} ${color.asHex()}"
  }

  companion object {
    fun placeholder() = ColorViewState(
      0, GENERATED,
      isFavorite = true,
      isLoading = false,
      isPlaceHolder = true
    )

    fun placeholders(): List<ColorViewState> = (1..2).map { placeholder() }
  }
}
