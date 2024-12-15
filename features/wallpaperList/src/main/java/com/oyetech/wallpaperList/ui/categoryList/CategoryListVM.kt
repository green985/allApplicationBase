package com.oyetech.wallpaperList.ui.categoryList;

import androidx.lifecycle.viewModelScope
import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.file.FileHelper
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.models.utils.moshi.deserializeList
import com.oyetech.models.utils.states.ViewState
import com.oyetech.models.wallpaperModels.helperModels.WallpaperCategoryProperty
import com.oyetech.models.wallpaperModels.requestBody.SearchRequestConsts
import com.oyetech.navigation.navigationFuncs.WallpaperNavigationFunc
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-28.02.2024-
-12:16-
 **/

class CategoryListVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {

    var categoryListLiveData = makeViewStateLiveData<List<WallpaperCategoryProperty>>()

    init {
        getCategoryJsonString()
    }

    fun getCategoryJsonString() {
        viewModelScope.launch(dispatcher.io) {

            var fileName = "popularTagList.json"

            var jsonFileString = FileHelper.readJsonFromAssets(context, fileName)

            var categoryList =
                jsonFileString.deserializeList<WallpaperCategoryProperty>()?.shuffled()

            if (categoryList.isNullOrEmpty()) {
                categoryListLiveData.postValue(ViewState.error(WallpaperLanguage.ERROR_LIST_EMPTY))
            } else {
                categoryListLiveData.postValue(ViewState.success(categoryList))
            }
        }

    }

    fun navigateWallpaperListWithTagItem(itemData: WallpaperCategoryProperty?) {
        if (itemData == null) {
            // do nothing
            return
        }

        var tagQueryString = SearchRequestConsts.categoryQueryString + itemData.tagId

        navigate(
            WallpaperNavigationFunc.actionToWallpaperFragmentFromCategory(
                tagQueryString,
                categoryName = itemData.tagName
            )
        )
    }

}