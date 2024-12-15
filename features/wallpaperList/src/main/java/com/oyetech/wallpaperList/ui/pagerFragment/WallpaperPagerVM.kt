package com.oyetech.wallpaperList.ui.pagerFragment;

import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.models.wallpaperModels.requestBody.SearchRequestConsts
import com.oyetech.models.wallpaperModels.requestBody.SearchRequestConsts.sortingMap

/**
Created by Erdi Özbek
-23.02.2024-
-15:42-
 **/

class WallpaperPagerVM(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {
    fun getWallPaperListSearchKey(): List<String> {
        return SearchRequestConsts.sortingMap.map {
            it.first
        }
    }

    fun getWallPaperListText(): List<String> {
        return SearchRequestConsts.sortingMap.map {
            it.second
        }
    }

    fun getTranslationWallpaperListText(): List<String> {
        val transformedSortingMap = sortingMap.map { pair ->
            pair.first to when (pair.second) {
                "Random" -> WallpaperLanguage.RANDOM
                "Views" -> WallpaperLanguage.VIEWS
                "Toplist" -> WallpaperLanguage.TOPLIST
                "Relevance" -> WallpaperLanguage.RELEVANCE
                "Date Added" -> WallpaperLanguage.DATE_ADDED
                "Favorites" -> WallpaperLanguage.FAVORITES
                else -> pair.second
            }
        }

        return transformedSortingMap.map {
            it.second
        }
    }

}