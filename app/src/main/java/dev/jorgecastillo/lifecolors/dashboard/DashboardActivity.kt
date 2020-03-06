package dev.jorgecastillo.lifecolors.dashboard

import android.os.Bundle
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.common.view.AuthenticationActivity

class DashboardActivity : AuthenticationActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_dashboard)
  }
}
