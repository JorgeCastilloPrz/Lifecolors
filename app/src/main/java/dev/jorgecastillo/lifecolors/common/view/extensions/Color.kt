package dev.jorgecastillo.lifecolors.common.view.extensions

import androidx.core.graphics.ColorUtils

/**
 * Check if a color is dark (convert to XYZ & check Y component)
 */
fun Int.isDark(): Boolean = ColorUtils.calculateLuminance(this) < 0.5
