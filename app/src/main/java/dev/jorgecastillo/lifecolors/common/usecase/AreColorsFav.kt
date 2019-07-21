package dev.jorgecastillo.lifecolors.common.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_COLORS_TABLE

class AreColorsFav {

  fun execute(colors: List<Int>, onFailure: () -> Unit, onSuccess: (Map<Int, Boolean>) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
      onFailure()
    } else {
      val db = FirebaseFirestore.getInstance()
      db.collection(USER_FAVED_COLORS_TABLE).document(userId)
        .get()
        .addOnSuccessListener { result ->
          val colorMap = result.data ?: mutableMapOf()
          val resultsMap: MutableMap<Int, Boolean> = mutableMapOf()
          colors.forEach {
            resultsMap[it] = colorMap[it.toString()] as? Boolean ?: false
          }

          onSuccess(resultsMap)
        }
        .addOnFailureListener { exception ->
          onFailure()
        }
    }
  }
}
