package dev.jorgecastillo.lifecolors.colorgeneration.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.detail.view.BottomCutout.Companion.DEFAULT_COLOR
import kotlinx.android.synthetic.main.activity_generated_colors.dot

class GeneratedColorsActivity : AppCompatActivity() {

  companion object {

    private const val PICKED_COLOR = "PICKED_COLORS"
    private const val PICKED_COLOR_POSITION = "PICKED_COLOR_POSITION"

    fun launch(
      source: Activity,
      sharedElement: View,
      pickedColor: Int,
      position: Int
    ) {
      val bundle = Bundle().apply {
        putInt(PICKED_COLOR, pickedColor)
        putInt(PICKED_COLOR_POSITION, position)
      }

      val intent = Intent(source, GeneratedColorsActivity::class.java)
      intent.putExtras(bundle)

      val options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(source, sharedElement, sharedElement.transitionName)
      ActivityCompat.startActivity(source, intent, options.toBundle())
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_generated_colors)
    postponeEnterTransition()

    val selectedColor = intent?.extras?.getInt(PICKED_COLOR, DEFAULT_COLOR) ?: DEFAULT_COLOR
    val position = intent?.extras?.getInt(PICKED_COLOR_POSITION, 0) ?: 0
    dot.color = selectedColor
    dot.transitionName = "$selectedColor$position"

    startPostponedEnterTransition()
  }
}
