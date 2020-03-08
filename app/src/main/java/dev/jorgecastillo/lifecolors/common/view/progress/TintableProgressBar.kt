package dev.jorgecastillo.lifecolors.common.view.progress

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import dev.jorgecastillo.lifecolors.R
import dev.jorgecastillo.lifecolors.common.view.progress.TintableProgressBar.IndeterminateStyles.MEDIUM
import dev.jorgecastillo.lifecolors.common.view.progress.TintableProgressBar.IndeterminateStyles.THIN

@SuppressLint("CustomViewStyleable")
internal class TintableProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.progressBarStyle
) : android.widget.ProgressBar(context, attrs, defStyleAttr) {

    private enum class IndeterminateStyles {
        THIN,
        MEDIUM
    }

    private var indeterminateStyle = THIN

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TintableProgressBar)
        val defaultColor = ContextCompat.getColor(context, R.color.colorAccent)
        val indeterminateTint =
            typedArray.getColor(R.styleable.TintableProgressBar_indeterminateTint, defaultColor)

        val styleOrdinal = typedArray.getInt(R.styleable.TintableProgressBar_indeterminateStyle, 0)
        indeterminateStyle = when (styleOrdinal) {
            1 -> MEDIUM
            else -> THIN
        }

        setupIndeterminateDrawable(indeterminateStyle, indeterminateTint)

        typedArray.recycle()
    }

    private fun setupIndeterminateDrawable(style: IndeterminateStyles, indeterminateTint: Int) {
        indeterminateDrawable = CircularProgressDrawable(context).apply {
            strokeWidth = when (style) {
                THIN -> resources.getDimensionPixelSize(R.dimen.progress_thin_stroke_width)
                    .toFloat()
                MEDIUM -> resources.getDimensionPixelSize(R.dimen.progress_medium_stroke_width)
                    .toFloat()
            }
        }

        setProgressBarTint(indeterminateTint)
    }

    fun setProgressBarTint(@ColorInt color: Int) {
        (indeterminateDrawable as CircularProgressDrawable).setColorSchemeColors(color)
    }
}
