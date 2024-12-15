package com.oyetech.domain.repository.viewHelperRepositories

import android.app.Activity
import android.widget.ImageView
import com.oyetech.models.wallpaperModels.helperModels.image.ImageViewerPropertyData

/**
Created by Erdi Özbek
-22.02.2024-
-16:55-
 **/

interface ImageViewerRepository {
    fun initImageViewerWithTransition(
        context: Activity,
        imageViewerPropertyList: ArrayList<ImageViewerPropertyData>,
        adapterPosition: Int,
        transitionImageView: ImageView?,
        overlayRepository: ImageViewerOverlayRepository,
    )

    fun onCleared()
}

interface ImageViewerOverlayRepository {
    fun moreLikeThisButtonHandler(imagePropertyData: ImageViewerPropertyData?)
    fun requestDownloadPermission()
}