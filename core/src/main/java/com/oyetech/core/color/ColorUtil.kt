package com.oyetech.core.color

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

/**
 * Color mixer.
 * Can use for something, I don't know...
 *
 */
object ColorUtil {
    fun blendARGB(
        @ColorInt color1: Int,
        @ColorInt color2: Int,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float
    ): Int {
        val inverseRatio = 1 - ratio
        val a = alpha(color1) * inverseRatio + alpha(color2) * ratio
        val r = red(color1) * inverseRatio + red(color2) * ratio
        val g = green(color1) * inverseRatio + green(color2) * ratio
        val b = blue(color1) * inverseRatio + blue(color2) * ratio
        return argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    private fun alpha(color: Int): Int {
        return color ushr 24
    }

    private fun red(color: Int): Int {
        return color shr 16 and 0xFF
    }

    private fun green(color: Int): Int {
        return color shr 8 and 0xFF
    }

    private fun blue(color: Int): Int {
        return color and 0xFF
    }

    private fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int {
        return alpha shl 24 or (red shl 16) or (green shl 8) or blue
    }
}
