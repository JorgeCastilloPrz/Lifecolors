package dev.jorgecastillo.lifecolors.palettes

import dev.jorgecastillo.lifecolors.common.view.model.ColorType.PICKED
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState

fun Int.toColorDetails() = ColorViewState(
  color = this,
  type = PICKED,
  isFavorite = false
)

