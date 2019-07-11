package dev.jorgecastillo.lifecolors.palettes

import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorDetails

fun Int.toColorDetails() = ColorDetails(
  color = this
)

