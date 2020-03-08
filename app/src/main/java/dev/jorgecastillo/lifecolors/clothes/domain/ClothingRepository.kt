package dev.jorgecastillo.lifecolors.clothes.domain

import dev.jorgecastillo.androidcolorx.library.HEXColor
import dev.jorgecastillo.lifecolors.clothes.network.FirebaseClothingDatabase
import dev.jorgecastillo.lifecolors.clothes.network.ZalandoNetworkService
import dev.jorgecastillo.zalandoclient.ZalandoApiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalCoroutinesApi
class ClothingRepository(
    private val zalandoNetworkService: ZalandoNetworkService = ZalandoNetworkService(),
    private val firebaseClothingDatabase: FirebaseClothingDatabase = FirebaseClothingDatabase()
) {

    suspend fun get(
        category: ZalandoApiClient.ZalandoCategory,
        color: HEXColor
    ): Flow<List<ClothingItem>> {
        val networkItemsFlow = zalandoNetworkService.get(category, color)
        val favedItemIdsFlow = firebaseClothingDatabase.getFavedItemIds().distinctUntilChanged()

        return networkItemsFlow.combine(favedItemIdsFlow) { networkItems, favedItems ->
            networkItems.map { networkItem ->
                networkItem.copy(isFaved = networkItem.idIn(favedItems))
            }
        }
    }

    suspend fun getFavedItems(category: ZalandoApiClient.ZalandoCategory): Flow<List<ClothingItem>> =
        firebaseClothingDatabase.getFavedItems(category).distinctUntilChanged()

    private fun ClothingItem.idIn(items: List<String>): Boolean =
        items.any { it == this.id }

    suspend fun toggleItemFav(clothingItem: ClothingItem): Unit {
        firebaseClothingDatabase.toggleItemFav(clothingItem)
    }
}
