package com.oyetech.core.appUtil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.io.File

/**
Created by Erdi Özbek
-14.05.2022-
-15:33-
 **/

object BitmapUtil {

    fun getWidthHeightFromUri(context: Context, filePath: String): Pair<Float, Float>? {
        if (filePath.isBlank()) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            context.getContentResolver().openInputStream(File(filePath).toUri()),
            null,
            options
        )
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth
        return Pair(imageWidth.toFloat(), imageHeight.toFloat())
    }

    fun getBitmapFromDrawableResource(context: Context, @DrawableRes resId: Int): Bitmap? {
        return ContextCompat.getDrawable(context, resId)?.toBitmap()
    }

    fun Drawable.toBitmap(): Bitmap {
        if (this is BitmapDrawable) {
            return bitmap
        }

        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)

        return bitmap
    }
}
