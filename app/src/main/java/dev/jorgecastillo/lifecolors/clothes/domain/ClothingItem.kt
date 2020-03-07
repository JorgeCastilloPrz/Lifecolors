package dev.jorgecastillo.lifecolors.clothes.domain

data class ClothingItem(
    val imageUrl: String,
    val url: String,
    val name: String,
    val price: String,
    val isFaved: Boolean
)
