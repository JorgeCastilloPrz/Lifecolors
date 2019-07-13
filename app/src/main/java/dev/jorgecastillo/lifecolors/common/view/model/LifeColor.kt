package dev.jorgecastillo.lifecolors.common.view.model

import androidx.annotation.ColorInt
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.GENERATED
import dev.jorgecastillo.lifecolors.common.view.model.ColorType.PICKED

enum class ColorType {
  PICKED,
  GENERATED
}

data class LifeColor(@ColorInt val color: Int, val type: ColorType)

fun Int.toPickedLifeColor() = LifeColor(this, PICKED)
fun Int.toGeneratedLifeColor() = LifeColor(this, GENERATED)
