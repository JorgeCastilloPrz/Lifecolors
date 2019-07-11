package dev.jorgecastillo.lifecolors.palettes

import android.graphics.Bitmap
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class GlideSimpleBitmapListener(private val onLoaded: (Bitmap) -> Unit) : RequestListener<Bitmap> {
  override fun onLoadFailed(
    e: GlideException?,
    model: Any,
    target: Target<Bitmap>,
    isFirstResource: Boolean
  ): Boolean {
    return true
  }

  override fun onResourceReady(
    resource: Bitmap,
    model: Any,
    target: Target<Bitmap>,
    dataSource: DataSource,
    isFirstResource: Boolean
  ): Boolean {
    onLoaded(resource)
    return true
  }
}
