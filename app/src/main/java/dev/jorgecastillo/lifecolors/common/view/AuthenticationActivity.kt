package dev.jorgecastillo.lifecolors.common.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.util.ExtraConstants
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import dev.jorgecastillo.lifecolors.R

abstract class AuthenticationActivity : AppCompatActivity() {

  companion object {
    const val RC_SIGN_IN = 991
  }

  private lateinit var onAuthenticationFailed: () -> Unit
  private lateinit var onAuthenticationSuccess: () -> Unit

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    catchFirebaseEmailLink()
  }

  private fun getAvailableProviders(): List<IdpConfig> {
    val actionCodeSettings = ActionCodeSettings.newBuilder()
      .setAndroidPackageName("dev.jorgecastillo.lifecolors", true, null)
      .setHandleCodeInApp(true)
      .setUrl("https://lifecolors.app")
      .build()

    return arrayListOf(
      AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn().setActionCodeSettings(actionCodeSettings).build(),
      AuthUI.IdpConfig.GoogleBuilder().build(),
      AuthUI.IdpConfig.FacebookBuilder().build()
    )
  }

  private fun catchFirebaseEmailLink() {
    if (AuthUI.canHandleIntent(intent)) {
      if (intent.extras == null) {
        return
      }
      val link = intent.extras!!.getString(ExtraConstants.EMAIL_LINK_SIGN_IN)
      if (link != null) {
        startActivityForResult(
          AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setEmailLink(link)
            .setAvailableProviders(getAvailableProviders())
            .build(),
          RC_SIGN_IN
        )
      }
    }
  }

  fun logout() {
    AuthUI.getInstance()
      .signOut(this)
      .addOnCompleteListener {
        Toast.makeText(this, R.string.successful_logout, Toast.LENGTH_SHORT).show()
      }
  }

  fun authenticate(onAuthenticationFailed: () -> Unit = {}, onAuthenticationSuccess: () -> Unit = {}) {
    this.onAuthenticationFailed = onAuthenticationFailed
    this.onAuthenticationSuccess = onAuthenticationSuccess

    if (FirebaseAuth.getInstance().currentUser == null) {
      AlertDialog.Builder(this)
        .setTitle(R.string.firebase_auth_confirmation_title)
        .setMessage(R.string.firebase_auth_confirmation_msg)
        .setNegativeButton(android.R.string.cancel) { _, _ -> }
        .setPositiveButton(android.R.string.ok) { _, _ ->
          performAuthentication()
        }
        .show()
    } else {
      onAuthenticationSuccess()
    }
  }

  private fun performAuthentication() {
    startActivityForResult(
      AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(getAvailableProviders())
        .setLogo(R.mipmap.ic_launcher_round)
        .setTheme(R.style.AuthenticationScreen)
        .build(),
      RC_SIGN_IN
    )
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == RC_SIGN_IN) {
      val response = IdpResponse.fromResultIntent(data)

      if (resultCode == Activity.RESULT_OK) {
        // Successfully signed in
        val user = FirebaseAuth.getInstance().currentUser!!
        onAuthenticationSuccess()

        Toast.makeText(this, R.string.successful_login, Toast.LENGTH_SHORT).show()
        // ...
      } else {
        // If response is null the user canceled the sign-in flow using the back button.
        if (response != null) {
          Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show()
          onAuthenticationFailed()
          // Can check response.getError().getErrorCode() to handle the error.
        }
      }
    }
  }
}
