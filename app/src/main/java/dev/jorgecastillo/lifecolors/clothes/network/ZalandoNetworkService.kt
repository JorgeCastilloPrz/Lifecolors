package dev.jorgecastillo.lifecolors.clothes.network

import dev.jorgecastillo.androidcolorx.library.HEXColor
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.lifecolors.clothes.network.mappers.toDomain
import dev.jorgecastillo.zalandoclient.ZalandoApiClient

class ZalandoNetworkService(
    private val zalandoApiClient: ZalandoApiClient = ZalandoApiClient()
) {

    fun get(category: ZalandoApiClient.ZalandoCategory, color: HEXColor): List<ClothingItem> {
        return zalandoApiClient.get(category, color).map { it.toDomain(true) }
    }
}
