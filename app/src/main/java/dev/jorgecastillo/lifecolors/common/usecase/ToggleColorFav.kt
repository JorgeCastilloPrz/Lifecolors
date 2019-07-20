package dev.jorgecastillo.lifecolors.common.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.jorgecastillo.lifecolors.common.data.FirebaseTables.USER_FAVED_COLORS_TABLE

class ToggleColorFav {

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

          colorMap[color.toString()] = !currentValueForColor
          storeColors(userId, colorMap, !currentValueForColor, onFailure, onSuccess)
        }
        .addOnFailureListener { exception ->
          onFailure()
        }
    }
  }

  private fun storeColors(
    userId: String,
    colorMap: Map<String, Any>,
    result: Boolean,
    onFailure: () -> Unit,
    onSuccess: (Boolean) -> Unit
  ) {
    val db = FirebaseFirestore.getInstance()
    db.collection(USER_FAVED_COLORS_TABLE).document(userId)
      .set(colorMap)
      .addOnSuccessListener { documentReference ->
        onSuccess(result)
      }
      .addOnFailureListener { e ->
        onFailure()
      }
  }
}
