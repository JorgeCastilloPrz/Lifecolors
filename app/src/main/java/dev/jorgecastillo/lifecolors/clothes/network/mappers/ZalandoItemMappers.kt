package dev.jorgecastillo.lifecolors.clothes.network.mappers

import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.zalandoclient.ZalandoItem

fun ZalandoItem.toDomain(isFaved: Boolean? = null): ClothingItem =
    ClothingItem(imageUrl, url, brandName, articleName, price, isFaved)
