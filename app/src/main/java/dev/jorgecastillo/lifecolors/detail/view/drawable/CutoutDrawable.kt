package dev.jorgecastillo.lifecolors.detail.view.drawable

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dev.jorgecastillo.lifecolors.R

class CutoutDrawable(context: Context) : MaterialShapeDrawable() {

  private val maxCornerRadius =
    context.resources.getDimensionPixelSize(R.dimen.cutout_bottom_sheet_radius)

  init {
    fillColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
    bindProgress(0f)
  }

  fun bindProgress(progress: Float) {
    val progressLeft = 1 - progress
    val currentRadius = progressLeft * maxCornerRadius
    shapeAppearanceModel = ShapeAppearanceModel.Builder()
      .setAllCornerSizes(0f)
      .setTopLeftCornerSize(currentRadius)
      .build()
  }
}
