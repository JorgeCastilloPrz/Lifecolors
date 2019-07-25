package dev.jorgecastillo.lifecolors.palettes

import dev.jorgecastillo.lifecolors.common.view.model.ColorType
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.PICKED
import dev.jorgecastillo.lifecolors.palettes.domain.model.ColorViewState

fun Int.toColorDetails(type: ColorType = PICKED) = ColorViewState(
  color = this,
  type = type,
  isFavorite = false,
  isLoading = false
)

fun List<Int>.toPickedColorDetails() = map { it.toColorDetails(type = PICKED) }

fun List<Int>.toGeneratedColorDetails() = map { it.toColorDetails(type = GENERATED) }

