package dev.jorgecastillo.lifecolors.common.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_COLORS_TABLE
import kotlinx.coroutines.tasks.await

sealed class IsColorFavResult {
  data class Success(val isFavorite: Boolean) : IsColorFavResult()
  object Error : IsColorFavResult()
}

class IsColorFav : UseCase<Int, IsColorFavResult> {

  override suspend fun execute(input: Int): IsColorFavResult {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    return if (userId == null) {
      IsColorFavResult.Error
    } else {
      val db = FirebaseFirestore.getInstance()
      try {
        val result = db.collection(USER_FAVED_COLORS_TABLE).document(userId).get().await()
        val colorMap = result.data ?: mutableMapOf()
        val currentValueForColor = colorMap[input.toString()] as? Boolean ?: false
        IsColorFavResult.Success(currentValueForColor)
      } catch (e: FirebaseFirestoreException) {
        IsColorFavResult.Error
      }
    }
  }
}
