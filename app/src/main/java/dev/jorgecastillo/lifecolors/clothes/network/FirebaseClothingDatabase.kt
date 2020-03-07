package dev.jorgecastillo.lifecolors.clothes.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_CLOTHES
import dev.jorgecastillo.lifecolors.common.data.errors.FirebaseUserNotAuthenticatedException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@ExperimentalCoroutinesApi
class FirebaseClothingDatabase {

    @Suppress("RemoveExplicitTypeArguments")
    suspend fun getFavedItemIds(): Flow<List<String>> = callbackFlow {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            offer(emptyList<String>())
        } else {
            val eventDocument = FirebaseFirestore.getInstance()
                .collection(USER_FAVED_CLOTHES)
                .document(userId)

            val subscription = eventDocument.addSnapshotListener { snapshot, _ ->
                if (snapshot!!.exists()) {
                    val favedMap: Map<String, Any> = snapshot.data ?: mapOf<String, Any>()
                    offer(favedMap.filterValues { faved -> faved as Boolean }.keys.toList())
                } else {
                    offer(emptyList<String>())
                }
            }

            awaitClose { subscription.remove() }
        }
    }

    @Suppress("RemoveExplicitTypeArguments")
    suspend fun toggleItemFav(id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            throw FirebaseUserNotAuthenticatedException
        } else {
            val result = FirebaseFirestore.getInstance()
                .collection(USER_FAVED_CLOTHES)
                .document(userId)
                .get()
                .await()

            val favedMap: MutableMap<String, Any> = result.data ?: mutableMapOf<String, Any>()
            val currentValueForId = favedMap[id] as? Boolean ?: false
            favedMap[id] = !currentValueForId

            storeFavedClothes(userId, favedMap)
        }
    }
}

private suspend fun storeFavedClothes(userId: String, favedMap: Map<String, Any>) {
    val db = FirebaseFirestore.getInstance()
    db.collection(USER_FAVED_CLOTHES).document(userId).set(favedMap).await()
}
