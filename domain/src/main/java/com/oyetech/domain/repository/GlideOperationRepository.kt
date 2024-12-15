package com.oyetech.domain.repository

import android.content.Context
import android.graphics.Bitmap

/**
Created by Erdi Özbek
-19.01.2023-
-23:55-
 **/

interface GlideOperationRepository {
    fun getBitmapWithUrl(url: String?, bitmapLoadAction: (Bitmap?) -> Unit)
    fun setUseCaseContext(context: Context)
}