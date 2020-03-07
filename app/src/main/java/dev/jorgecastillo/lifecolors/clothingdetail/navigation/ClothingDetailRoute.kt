package dev.jorgecastillo.lifecolors.clothingdetail.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem

fun Context.launchClothingItemDetail(item: ClothingItem) {
    val url = item.url
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}
