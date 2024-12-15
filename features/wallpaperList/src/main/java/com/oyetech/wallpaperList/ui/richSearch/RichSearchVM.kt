package com.oyetech.wallpaperList.ui.richSearch;

import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.useCases.wallpaperApp.WallpaperSearchOperationUseCase
import com.oyetech.models.wallpaperModels.requestBody.CategoryTags
import com.oyetech.models.wallpaperModels.requestBody.PurityTags
import timber.log.Timber

/**
Created by Erdi Özbek
-15.02.2024-
-13:36-
 **/

class RichSearchVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {

    lateinit var wallpaperSearchOperationUseCase: WallpaperSearchOperationUseCase

    fun fileTypeValue(isJpg: Boolean, isChecked: Boolean) {
        wallpaperSearchOperationUseCase.setFileTypeQuery(isJpg, isChecked)
    }

    fun purityTypeValueWithTag(purityTag: PurityTags, isChecked: Boolean) {
        wallpaperSearchOperationUseCase.purityTypeValueWithTag(purityTag, isChecked)
    }

    fun categoryTypeValue(categoryTags: CategoryTags, isChecked: Boolean) {
        Timber.d("tagggg == " + categoryTags.toString())
        Timber.d("tagggg isChecked== " + isChecked)
        wallpaperSearchOperationUseCase.categoryTypeValue(categoryTags, isChecked)

    }

    fun orderTypeValue(isDesc: Boolean, isChecked: Boolean) {
        wallpaperSearchOperationUseCase.orderTypeValue(isDesc, isChecked)

    }

    fun resolutionTypeValue(systemResolutionText: String, isChecked: Boolean) {
        wallpaperSearchOperationUseCase.resolutionTypeValue(systemResolutionText, isChecked)

    }

    fun colorTypeValueWithTag(colorString: String, isChecked: Boolean) {
        wallpaperSearchOperationUseCase.colorTypeValueWithTag(
            colorString = colorString,
            isChecked = isChecked
        )

    }

    fun resolutionAtLeastExactlyTypeValue(isAtLeast: Boolean, isChecked: Boolean) {
        wallpaperSearchOperationUseCase.resolutionAtLeastExactlyTypeValue(
            isAtLeast = isAtLeast,
            isChecked = isChecked
        )
    }

    fun resolutionRatioTypeValue(ratio: String, isChecked: Boolean) {

        wallpaperSearchOperationUseCase.resolutionRatioTypeValue(
            ratio = ratio,
            isChecked = isChecked
        )

    }

}