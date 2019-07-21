package dev.jorgecastillo.lifecolors.common.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_COLORS_TABLE

class IsColorFav {

  fun execute(color: Int, onFailure: () -> Unit, onSuccess: (Boolean) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
      onFailure()
    } else {
      val db = FirebaseFirestore.getInstance()
      db.collection(USER_FAVED_COLORS_TABLE).document(userId)
        .get()
        .addOnSuccessListener { result ->
          val colorMap = result.data ?: mutableMapOf()
          val currentValueForColor = colorMap[color.toString()] as? Boolean ?: false
          onSuccess(currentValueForColor)
        }
        .addOnFailureListener { exception ->
          onFailure()
        }
    }
  }
}
