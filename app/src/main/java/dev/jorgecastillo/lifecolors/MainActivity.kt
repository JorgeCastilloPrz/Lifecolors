package dev.jorgecastillo.lifecolors

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.jorgecastillo.lifecolors.common.view.AuthenticationActivity
import dev.jorgecastillo.lifecolors.detail.DetailActivity
import dev.jorgecastillo.lifecolors.favoritecolors.FavoriteColorsActivity
import kotlinx.android.synthetic.main.activity_main.bar
import kotlinx.android.synthetic.main.activity_main.captureButton
import kotlinx.android.synthetic.main.activity_main.viewFinder
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class MainActivity : AuthenticationActivity() {

  companion object {
    private const val PICK_IMAGE_REQUEST_CODE = 9329
    private const val RATIO_4_3_VALUE = 4.0 / 3.0
    private const val RATIO_16_9_VALUE = 16.0 / 9.0
  }

  private lateinit var displayManager: DisplayManager
  private val cameraExecutor = Executors.newSingleThreadExecutor()
  private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
  private var displayId = -1
  private var camera: Camera? = null
  private lateinit var imageCapture: ImageCapture
  private lateinit var preview: Preview

  private val displayListener = object : DisplayManager.DisplayListener {
    override fun onDisplayAdded(displayId: Int) = Unit
    override fun onDisplayRemoved(displayId: Int) = Unit
    override fun onDisplayChanged(displayId: Int) {
      if (displayId == this@MainActivity.displayId) {
        Log.d("CameraX", "Rotation changed: ${viewFinder.display.rotation}")
        imageCapture.targetRotation = viewFinder.display.rotation
      }
    }
  }

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

  override fun onDestroy() {
    super.onDestroy()
    cameraExecutor.shutdown()
    displayManager.unregisterDisplayListener(displayListener)
  }

  private fun startCamera() {
    displayId = viewFinder.display.displayId

    // Create configuration object for the viewfinder use case
    viewFinder.post {
      displayId = viewFinder.display.displayId
      bindCameraUI()
      bindCameraUseCases()
    }
  }

  private fun aspectRatio(width: Int, height: Int): Int {
    val previewRatio = max(width, height).toDouble() / min(width, height)
    return if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
      AspectRatio.RATIO_4_3
    } else {
      AspectRatio.RATIO_16_9
    }
  }

  /** Declare and bind preview, capture and analysis use cases */
  private fun bindCameraUseCases() {
    // Get screen metrics used to setup camera for full screen resolution
    val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
    val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

    val rotation = viewFinder.display.rotation

    // Bind the CameraProvider to the LifeCycleOwner
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener(Runnable {
      val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

      preview = Preview.Builder()
        // We request aspect ratio but no resolution
        .setTargetAspectRatio(screenAspectRatio)
        // Set initial target rotation
        .setTargetRotation(rotation)
        .build()

      // setLensFacing(LensFacing.BACK)

      // Attach the viewfinder's surface provider to preview use case
      preview.setSurfaceProvider(viewFinder.previewSurfaceProvider)

      // ImageCapture
      imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        // We request aspect ratio but no resolution to match preview config, but letting
        // CameraX optimize for whatever specific resolution best fits our use cases
        .setTargetAspectRatio(screenAspectRatio)
        // Set initial target rotation, we will have to call this again if rotation changes
        // during the lifecycle of this use case
        .setTargetRotation(rotation)
        .build()

      // setLensFacing(LensFacing.BACK)

      // Must unbind the use-cases before rebinding them
      cameraProvider.unbindAll()

      try {
        // A variable number of use-cases can be passed here -
        // camera provides access to CameraControl & CameraInfo
        camera = cameraProvider.bindToLifecycle(
          this,
          cameraSelector,
          preview, imageCapture
        )
      } catch (exc: Exception) {
        Log.e("CameraX", "Use case binding failed", exc)
      }

    }, ContextCompat.getMainExecutor(this))
  }

  private fun bindCameraUI() {
    captureButton.setOnClickListener {
      captureButton.isEnabled = false
      captureImage()
    }
  }

  private fun captureImage() {
    val file = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
      outputOptions,
      cameraExecutor,
      object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(output: OutputFileResults) {
          val savedUri = output.savedUri ?: Uri.fromFile(file)
          viewFinder.post {
            DetailActivity.launch(this@MainActivity, captureButton, savedUri)
          }
        }

        override fun onError(exception: ImageCaptureException) {
          val msg = "Photo capture failed: ${exception.message}"
          Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
            .show()
          exception.printStackTrace()
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
      R.id.actionSettings -> true.also { showSettingsPopup() }
      R.id.actionFavoriteColors -> true.also { navigateToFavoriteColorsScreen() }
      R.id.actionGallery -> true.also { selectPictureFromGallery() }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun showSettingsPopup() {
    val anchorView = findViewById<View>(R.id.actionSettings)
    val popup = PopupMenu(this, anchorView)
    val options = resources.getStringArray(R.array.main_menu_actions)
    options.forEachIndexed { index, action ->
      popup.menu.add(0, index, index, action)
    }
    popup.setOnMenuItemClickListener {
      when (it.itemId) {
        0 -> {
        }
        1 -> logout()
      }
      true
    }
    popup.show()
  }

  private fun navigateToFavoriteColorsScreen() {
    FavoriteColorsActivity.launch(this)
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
