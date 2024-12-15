package com.oyetech.wallpaperList.ui.richSearch.helper

import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment

/**
Created by Erdi Özbek
-20.02.2024-
-11:50-
 **/

fun RichSearchFragment.prepareRatioTypeProperty() {
    binding.tvLabelRatioName.text = WallpaperLanguage.RATIO

    binding.csRatioSelect.repository = this
}