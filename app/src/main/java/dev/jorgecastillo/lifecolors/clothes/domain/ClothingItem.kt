package dev.jorgecastillo.lifecolors.clothes.domain

import dev.jorgecastillo.zalandoclient.ZalandoApiClient

data class ClothingItem(
  val imageUrl: String,
  val url: String,
  val brandName: String,
  val articleName: String,
  val price: String,
  val isFaved: Boolean? = null,
  val category: ZalandoApiClient.ZalandoCategory?,
  val isPlaceHolder: Boolean = false
) {
  val id = brandName + articleName

  companion object {
    fun placeholder() = ClothingItem(
      "", "", "", "", "", null, null, true
    )

    fun placeholders(): List<ClothingItem> = (1..3).map {
      placeholder()
    }
  }
}
