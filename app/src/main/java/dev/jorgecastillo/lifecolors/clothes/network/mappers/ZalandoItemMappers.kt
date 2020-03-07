package dev.jorgecastillo.lifecolors.clothes.network.mappers

import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.zalandoclient.ZalandoItem

fun ZalandoItem.toDomain(isFaved: Boolean): ClothingItem =
    ClothingItem(imageUrl, url, name, price, isFaved)
