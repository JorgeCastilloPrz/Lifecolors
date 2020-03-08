package dev.jorgecastillo.lifecolors.clothes.domain

import dev.jorgecastillo.zalandoclient.ZalandoApiClient

data class ClothingItem(
    val imageUrl: String,
    val url: String,
    val brandName: String,
    val articleName: String,
    val price: String,
    val isFaved: Boolean? = null,
    val category: ZalandoApiClient.ZalandoCategory?
) {
    val id = brandName + articleName
}
