package dev.jorgecastillo.lifecolors.clothes.network

import dev.jorgecastillo.androidcolorx.library.HEXColor
import dev.jorgecastillo.lifecolors.clothes.domain.ClothingItem
import dev.jorgecastillo.lifecolors.clothes.network.mappers.toDomain
import dev.jorgecastillo.zalandoclient.ZalandoApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ZalandoNetworkService(
    private val zalandoApiClient: ZalandoApiClient = ZalandoApiClient()
) {

    @ExperimentalCoroutinesApi
    fun get(category: ZalandoApiClient.ZalandoCategory, color: HEXColor): Flow<List<ClothingItem>> {
        return flow {
            val clothes = zalandoApiClient.get(category, color).map { it.toDomain() }
            emit(clothes)
        }
    }
}
