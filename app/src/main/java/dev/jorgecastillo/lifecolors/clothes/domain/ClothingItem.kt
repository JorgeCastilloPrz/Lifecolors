package dev.jorgecastillo.lifecolors.clothes.domain

data class ClothingItem(
    val imageUrl: String,
    val url: String,
    val brandName: String,
    val articleName: String,
    val price: String,
    val isFaved: Boolean? = null
) {
    val id = brandName + articleName
}
