package dev.jorgecastillo.lifecolors.common.domain.model

data class ColorDetails(val name: ColorName)
data class ColorName(val value: String, val closest_named_hex: String, val exact_match_name: Boolean, val distance: Int)

