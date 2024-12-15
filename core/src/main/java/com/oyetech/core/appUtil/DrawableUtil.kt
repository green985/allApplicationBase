package com.oyetech.core.appUtil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.oyetech.core.appUtil.ViewUtil.dpToPx
import com.oyetech.core.stringOperation.StringHelper

object DrawableUtil {
    val SHORTCUT_INFO_WRAPPED_SIZE = dpToPx(72)
    private val SHORTCUT_INFO_BITMAP_SIZE = dpToPx(108)
    private val SHORTCUT_INFO_PADDING = (SHORTCUT_INFO_BITMAP_SIZE - SHORTCUT_INFO_WRAPPED_SIZE) / 2
    fun toBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun wrapBitmapForShortcutInfo(toWrap: Bitmap): Bitmap {
        val bitmap =
            Bitmap.createBitmap(SHORTCUT_INFO_BITMAP_SIZE, SHORTCUT_INFO_BITMAP_SIZE, ARGB_8888)
        val scaled = Bitmap.createScaledBitmap(
            toWrap,
            SHORTCUT_INFO_WRAPPED_SIZE,
            SHORTCUT_INFO_WRAPPED_SIZE,
            true
        )
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(
            scaled,
            SHORTCUT_INFO_PADDING.toFloat(),
            SHORTCUT_INFO_PADDING.toFloat(),
            null
        )
        return bitmap
    }

    /**
     * Returns a new [Drawable] that safely wraps and tints the provided drawable.
     */
    fun tint(drawable: Drawable, @ColorInt tint: Int): Drawable {
        val tinted = DrawableCompat.wrap(drawable).mutate()
        DrawableCompat.setTint(tinted, tint)
        return tinted
    }

    fun getTintedDrawableWithResIdAndColorInt(
        context: Context,
        drawableRes: Int,
        colorInt: Int,
    ): Drawable {

        // Get the original drawable

        // Get the original drawable
        val originalDrawable = ContextCompat.getDrawable(context, drawableRes)

        // Wrap the drawable so it can be tinted

        // Wrap the drawable so it can be tinted
        val wrappedDrawable = DrawableCompat.wrap(
            originalDrawable!!
        )

        // Set the tint color (assuming tintColor is your desired color)
        var colorHexString = StringHelper.getColorString(colorInt)
        // Set the tint color (assuming tintColor is your desired color)
        val tintColor = Color.parseColor(colorHexString)
        DrawableCompat.setTint(wrappedDrawable, tintColor)

        // Set the tint mode if needed

        // Set the tint mode if needed
        DrawableCompat.setTintMode(wrappedDrawable, SRC_IN)

        return wrappedDrawable
    }


}