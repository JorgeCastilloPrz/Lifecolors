package dev.jorgecastillo.lifecolors.detail

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import dev.jorgecastillo.lifecolors.OnDotSelectedListener
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.detail.view.Dot
import dev.jorgecastillo.lifecolors.fadeIn
import dev.jorgecastillo.lifecolors.fadeOut
import dev.jorgecastillo.lifecolors.palettes.PalettesActivity
import dev.jorgecastillo.lifecolors.utils.GUIUtils
import dev.jorgecastillo.lifecolors.utils.OnRevealAnimationListener
import dev.jorgecastillo.lifecolors.utils.SimpleTransitionListener
import kotlinx.android.synthetic.main.activity_detail.activityRoot
import kotlinx.android.synthetic.main.activity_detail.bottomCutout
import kotlinx.android.synthetic.main.activity_detail.dotAnimationContainer
import kotlinx.android.synthetic.main.activity_detail.fab
import kotlinx.android.synthetic.main.activity_detail.overlay
import kotlinx.android.synthetic.main.activity_detail.picture

class DetailActivity : AppCompatActivity(), OnDotSelectedListener {

  companion object {
    const val FILE_URI_KEY = "FILE_URI_KEY"

    fun launch(
      source: Activity,
      sharedElement: View,
      uri: Uri
    ) {
      val bundle = Bundle().apply {
        putParcelable(FILE_URI_KEY, uri)
      }

      val intent = Intent(source, DetailActivity::class.java)
      intent.putExtras(bundle)
      val options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(source, sharedElement, sharedElement.transitionName)
      ActivityCompat.startActivity(source, intent, options.toBundle())
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_detail)
    setupEnterAnimation()
    setupOverlay()
  }

  private fun setupOverlay() {
    overlay.onDotSelectedListener = this
  }

  private fun setupEnterAnimation() {
    window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
      override fun onTransitionEnd(transition: Transition) {
        transition.removeListener(this)
        animateRevealShow(activityRoot)
      }
    })
  }

  private fun animateRevealShow(viewRoot: View) {
    val cx = (viewRoot.left + viewRoot.right) / 2
    val cy = (viewRoot.top + viewRoot.bottom) / 2
    GUIUtils.animateRevealShow(this, activityRoot, fab.width / 2, R.color.colorAccent,
      cx, cy, object : OnRevealAnimationListener {
        override fun onRevealHide() {
        }

        override fun onRevealShow() {
          initViews()
        }
      })
  }

  fun initViews() {
    intent?.extras?.getParcelable<Uri>(FILE_URI_KEY)
      ?.let { uri ->
        loadPicture(uri)
      }
  }

  private fun loadPicture(url: Uri): ImageViewTarget<Drawable> {
    return Glide.with(picture)
      .load(url)
      .apply(RequestOptions().fitCenter())
      .into<ImageViewTarget<Drawable>>(object : ImageViewTarget<Drawable>(picture) {
        override fun setResource(resource: Drawable?) {
          resource?.let { drawable ->
            val bitmap = (drawable as BitmapDrawable).bitmap
            picture.setImageBitmap(bitmap)
            picture.fadeIn()
            overlay.showTouchPopup()
            overlay.setBitmap(bitmap)
            overlay.fadeIn(700)
            bottomCutout.showPalette(bitmap)
            bottomCutout.setOnClickListener { navigateToPalettesActivity() }
          }
        }
      })
  }

  override fun onDotSelected(dot: Dot, x: Int, y: Int) {
    val renderedDot = renderDot(x, y, dot.color)
    animateDotToCutout(renderedDot, x, y)
  }

  private fun renderDot(x: Int, y: Int, dotColor: Int): Dot {
    val dotSize = resources.getDimensionPixelSize(R.dimen.dot_size)
    val dot = Dot(this).apply {
      this.color = dotColor
    }

    dotAnimationContainer.addView(dot, ConstraintLayout.LayoutParams(dotSize, dotSize).apply {
      topMargin = y - dotSize / 2
      leftMargin = x - dotSize / 2
    })
    return dot
  }

  private fun animateDotToCutout(dot: Dot, x: Int, y: Int) {
    val interpolator = AccelerateDecelerateInterpolator()
    dot.animate().translationX(bottomCutout.x + bottomCutout.getFirstCirclePosition().x - x)
      .setInterpolator(interpolator).start()
    dot.animate().translationY(bottomCutout.y + bottomCutout.getFirstCirclePosition().y - y)
      .setInterpolator(interpolator).withEndAction {
        dot.alpha = 0f
        dotAnimationContainer.removeView(dot)
      }.start()
    bottomCutout.addDotFirst(dot)
  }

  private fun navigateToPalettesActivity() {
    PalettesActivity.launch(this, bottomCutout, bottomCutout.pickedColors(), bottomCutout.generatedColors())
  }

  override fun onBackPressed() {
    picture.fadeOut()
    overlay.fadeOut()
    bottomCutout.fadeOut()

    GUIUtils.animateRevealHide(
      this,
      activityRoot,
      R.color.colorAccent,
      R.color.background,
      fab.width / 2,
      object : OnRevealAnimationListener {
        override fun onRevealHide() {
          backPressed()
        }

        override fun onRevealShow() {
        }
      })
  }

  fun backPressed() {
    finishAfterTransition()
  }
}
