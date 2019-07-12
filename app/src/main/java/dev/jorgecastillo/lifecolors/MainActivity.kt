package dev.jorgecastillo.lifecolors

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraX
import androidx.camera.core.CameraX.LensFacing
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import dev.jorgecastillo.lifecolors.detail.DetailActivity
import kotlinx.android.synthetic.main.activity_main.bar
import kotlinx.android.synthetic.main.activity_main.captureButton
import kotlinx.android.synthetic.main.activity_main.viewFinder
import java.io.File

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class MainActivity : AppCompatActivity() {

  companion object {
    private const val PICK_IMAGE_REQUEST_CODE = 9329
  }

  private lateinit var displayManager: DisplayManager
  private var displayId = -1

  /**
   * We need a display listener for orientation changes that do not trigger a configuration
   * change, for example if we choose to override config change in manifest or for 180-degree
   * orientation changes.
   */
  private val displayListener = object : DisplayManager.DisplayListener {
    override fun onDisplayAdded(displayId: Int) = Unit
    override fun onDisplayRemoved(displayId: Int) = Unit
    override fun onDisplayChanged(displayId: Int) {
      preview.setTargetRotation(viewFinder.display.rotation)
      imageCapture.setTargetRotation(viewFinder.display.rotation)
    }
  }

  private lateinit var previewConfig: PreviewConfig
  private lateinit var imageCaptureConfig: ImageCaptureConfig
  private lateinit var imageCapture: ImageCapture
  private lateinit var preview: Preview

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(bar)
    handlePermissions()
  }

  private fun handlePermissions() {
    if (allPermissionsGranted()) {
      viewFinder.post {
        startCamera()
      }
    } else {
      ActivityCompat.requestPermissions(
        this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
      )
    }
  }

  override fun onResume() {
    super.onResume()
    captureButton.isEnabled = true
  }

  override fun onStart() {
    super.onStart()
    displayManager = viewFinder.context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    displayManager.registerDisplayListener(displayListener, null)
  }

  override fun onStop() {
    super.onStop()
    displayManager.unregisterDisplayListener(displayListener)
  }

  private fun startCamera() {
    displayId = viewFinder.display.displayId

    // val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
    val screenAspectRatio = Rational(viewFinder.width, viewFinder.height)

    // Create configuration object for the viewfinder use case
    previewConfig = PreviewConfig.Builder()
      .apply {
        setLensFacing(LensFacing.BACK)
        setTargetAspectRatio(screenAspectRatio)
        setTargetRotation(viewFinder.display.rotation)
      }
      .build()

    preview = AutoFitPreviewBuilder.build(previewConfig, viewFinder)

    // Create configuration object for the image capture use case
    imageCaptureConfig = ImageCaptureConfig.Builder()
      .apply {
        setLensFacing(LensFacing.BACK)
        setTargetAspectRatio(screenAspectRatio)
        setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
        setTargetRotation(viewFinder.display.rotation)
      }
      .build()

    imageCapture = ImageCapture(imageCaptureConfig)
    captureButton.setOnClickListener {
      captureButton.isEnabled = false
      captureImage()
    }

    // Bind use cases to lifecycle
    CameraX.bindToLifecycle(this, preview, imageCapture)
  }

  private fun captureImage() {
    val file = File(
      externalMediaDirs.first(),
      "${System.currentTimeMillis()}.jpg"
    )
    imageCapture.takePicture(file,
      object : ImageCapture.OnImageSavedListener {
        override fun onError(
          error: ImageCapture.UseCaseError,
          message: String,
          exc: Throwable?
        ) {
          val msg = "Photo capture failed: $message"
          Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
            .show()
          exc?.printStackTrace()
        }

        override fun onImageSaved(file: File) {
          DetailActivity.launch(this@MainActivity, captureButton, file.toUri())
        }
      })
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    if (requestCode == REQUEST_CODE_PERMISSIONS) {
      if (allPermissionsGranted()) {
        viewFinder.post { startCamera() }
      } else {
        Toast.makeText(
          this,
          "Permissions not granted by the user.",
          Toast.LENGTH_SHORT
        )
          .show()
        finish()
      }
    }
  }

  /**
   * Check if all permission specified in the manifest have been granted
   */
  private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
      baseContext, it
    ) == PackageManager.PERMISSION_GRANTED
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.actionGallery -> true.also { selectPictureFromGallery() }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun selectPictureFromGallery() {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_PICK
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CODE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PICK_IMAGE_REQUEST_CODE) {
      data?.let {
        data.data?.let { uri ->
          captureButton.post {
            DetailActivity.launch(this@MainActivity, captureButton, uri)
          }
        }
      }
    }
  }
}
