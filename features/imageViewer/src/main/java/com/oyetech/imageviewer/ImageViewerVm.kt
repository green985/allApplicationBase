package com.oyetech.imageviewer

import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers

/**
Created by Erdi Özbek
-16.05.2022-
-13:22-
 **/

class ImageViewerVm(
    appDispatchers: AppDispatchers,
) : BaseViewModel(appDispatchers) {

    var imagePosition = 0

    var downloadedImageUrl = ""

    lateinit var imageUrlList: ArrayList<String>

    fun getImageUrlList(): List<String> {
        return imageUrlList
    }
}
