package dev.jorgecastillo.lifecolors.common.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_COLORS_TABLE
import kotlinx.coroutines.tasks.await

sealed class GetFavColorsResult {
  data class Success(val colors: List<Int>) : GetFavColorsResult()
  object Error : GetFavColorsResult()
}

class GetFavColors : UseCase<Unit, GetFavColorsResult> {

  override suspend fun execute(input: Unit): GetFavColorsResult {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
      return GetFavColorsResult.Error
    } else {
      val db = FirebaseFirestore.getInstance()
      return try {
        val favedColors = db.collection(USER_FAVED_COLORS_TABLE).document(userId).get().await()
        val colorMap = favedColors.data ?: mapOf()
        return GetFavColorsResult.Success(colorMap.toList().filter { it.second == true }.map { it.first.toInt() })
      } catch (e: FirebaseFirestoreException) {
        GetFavColorsResult.Error
      }
    }
  }
}
