package dev.jorgecastillo.lifecolors.common.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_COLORS_TABLE
import kotlinx.coroutines.tasks.await

sealed class ToggleColorFavResult {
  data class Success(val newFavState: Boolean) : ToggleColorFavResult()
  object Error : ToggleColorFavResult()
}

class ToggleColorFav : UseCase<Int, ToggleColorFavResult> {

  override suspend fun execute(input: Int): ToggleColorFavResult {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    return if (userId == null) {
      ToggleColorFavResult.Error
    } else {
      val db = FirebaseFirestore.getInstance()
      try {
        val result = db.collection(USER_FAVED_COLORS_TABLE).document(userId).get().await()
        val colorMap = result.data ?: mutableMapOf()
        val currentValueForColor = colorMap[input.toString()] as? Boolean ?: false

        colorMap[input.toString()] = !currentValueForColor
        if (storeColors(userId, colorMap)) {
          ToggleColorFavResult.Success(!currentValueForColor)
        } else {
          ToggleColorFavResult.Error
        }
      } catch (e: FirebaseFirestoreException) {
        ToggleColorFavResult.Error
      }
    }
  }

  private suspend fun storeColors(userId: String, colorMap: Map<String, Any>): Boolean {
    val db = FirebaseFirestore.getInstance()
    return try {
      db.collection(USER_FAVED_COLORS_TABLE).document(userId).set(colorMap).await()
      true
    } catch (e: FirebaseFirestoreException) {
      false
    }
  }
}
