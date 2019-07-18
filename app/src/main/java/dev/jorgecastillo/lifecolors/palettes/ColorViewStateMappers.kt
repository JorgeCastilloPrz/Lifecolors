package dev.jorgecastillo.lifecolors.palettes

import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState

fun Int.toColorDetails() = ColorViewState(
  color = this
)

