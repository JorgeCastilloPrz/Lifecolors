package dev.jorgecastillo.lifecolors.palettes.domain.model

import androidx.annotation.ColorInt
import dev.jorgecastillo.lifecolors.colorgeneration.view.toHex
import dev.jorgecastillo.lifecolors.common.view.model.ColorType

data class ColorViewState(
  @ColorInt val color: Int,
  val type: ColorType,
  val isFavorite: Boolean,
  val isLoading: Boolean) {

  override fun toString(): String {
    return "${super.toString()} ${color.toHex()}"
  }
}
