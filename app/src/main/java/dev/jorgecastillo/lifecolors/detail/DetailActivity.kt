package dev.jorgecastillo.lifecolors.detail

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.utils.GUIUtils
import dev.jorgecastillo.lifecolors.utils.OnRevealAnimationListener
import dev.jorgecastillo.lifecolors.utils.SimpleTransitionListener
import kotlinx.android.synthetic.main.activity_detail.activityRoot
import kotlinx.android.synthetic.main.activity_detail.fab
import kotlinx.android.synthetic.main.activity_detail.overlay
import kotlinx.android.synthetic.main.activity_detail.picture
import java.io.File

class DetailActivity : AppCompatActivity() {

  companion object {
    const val FILE_NAME_KEY = "FILE_NAME_KEY"

    fun launch(
      source: Activity,
      sharedElement: View,
      image: File
    ) {
      val bundle = Bundle().apply {
        putString(FILE_NAME_KEY, image.absolutePath)
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
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private fun setupEnterAnimation() {
    val transition = TransitionInflater.from(this)
      .inflateTransition(R.transition.changebounds_with_arcmotion)
    window.sharedElementEnterTransition = transition

    transition.addListener(object : SimpleTransitionListener() {
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
    intent?.extras?.getString(FILE_NAME_KEY)
      ?.let { File(it) }
      .let {
        Glide.with(picture)
          .load(it)
          .apply(RequestOptions().fitCenter())
          .into<ImageViewTarget<Drawable>>(object : ImageViewTarget<Drawable>(picture) {
            override fun setResource(resource: Drawable?) {
              resource?.let { drawable ->
                val bitmap = (drawable as BitmapDrawable).bitmap
                picture.setImageBitmap(bitmap)
                picture.animate()
                  .alpha(1f)
                  .setDuration(150)
                  .start()
                overlay.generateRandomDots(bitmap)
                overlay.animate()
                  .alpha(1f)
                  .setDuration(150)
                  .start()
              }
            }
          })
      }
  }

  override fun onBackPressed() {
    picture.animate()
      .alpha(0f)
      .setDuration(150)
      .start()
    overlay.animate()
      .alpha(0f)
      .setDuration(150)
      .start()
    GUIUtils.animateRevealHide(
      this,
      activityRoot,
      R.color.colorAccent,
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
    val fade = Fade()
    window.returnTransition = fade
    fade.duration = resources.getInteger(R.integer.animation_duration)
      .toLong()
    finishAfterTransition()
  }
}
