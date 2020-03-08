package dev.jorgecastillo.lifecolors.clothes.network.mappers

import com.squareup.moshi.Moshi
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.zalandoclient.ZalandoItem

fun ZalandoItem.toDomain(isFaved: Boolean? = null): ClothingItem =
    ClothingItem(imageUrl, url, brandName, articleName, price, isFaved)

fun String.deserializeItem(): ClothingItem {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(ClothingItem::class.java)
    return jsonAdapter.fromJson(this)!!
}

fun ClothingItem.serialize(): String {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(ClothingItem::class.java)
    return jsonAdapter.toJson(this)!!
}
