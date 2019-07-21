package dev.jorgecastillo.lifecolors.common.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_COLORS_TABLE
import kotlinx.coroutines.tasks.await

sealed class AreColorsFavResult {
  data class Success(val colorsWithFavStatus: Map<Int, Boolean>) : AreColorsFavResult()
  object Error : AreColorsFavResult()
}

class AreColorsFav : UseCase<List<Int>, AreColorsFavResult> {
  
  override suspend fun execute(input: List<Int>): AreColorsFavResult {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
      return AreColorsFavResult.Error
    } else {
      val db = FirebaseFirestore.getInstance()
      return try {
        val favedColors = db.collection(USER_FAVED_COLORS_TABLE).document(userId).get().await()
        val colorMap = favedColors.data ?: mutableMapOf()
        val resultsMap: MutableMap<Int, Boolean> = mutableMapOf()

        input.forEach {
          resultsMap[it] = colorMap[it.toString()] as? Boolean ?: false
        }

        AreColorsFavResult.Success(resultsMap)
      } catch (e: FirebaseFirestoreException) {
        AreColorsFavResult.Error
      }
    }
  }
}
