package com.oyetech.wallpaperList.di

import com.oyetech.wallpaperList.ui.wallpaperList.WallpaperListVM
import com.oyetech.wallpaperList.ui.categoryList.CategoryListVM
import com.oyetech.wallpaperList.ui.pagerFragment.WallpaperPagerVM
import com.oyetech.wallpaperList.ui.richSearch.RichSearchVM
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-7.02.2024-
-20:57-
 **/

object WallpaperListModule {

    var wallpaperListModule = module {
        viewModelOf(::WallpaperListVM)
        viewModelOf(::RichSearchVM)
        viewModelOf(::WallpaperPagerVM)
        viewModelOf(::CategoryListVM)
    }

}