package com.oyetech.domain.useCases

import android.content.Context
import android.graphics.Bitmap
import com.oyetech.domain.repository.GlideOperationRepository

/**
Created by Erdi Özbek
-20.01.2023-
-00:12-
 **/

class GlideOperationUseCase(private var repository: GlideOperationRepository) {
    fun getBitmapWithUrl(url: String?, bitmapLoadAction: ((Bitmap?) -> Unit)) {
        repository.getBitmapWithUrl(url, bitmapLoadAction)
    }

    fun setUseCaseContext(context: Context) {
        repository.setUseCaseContext(context)
    }


}