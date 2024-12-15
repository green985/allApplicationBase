package com.oyetech.wallpaperList.ui.richSearch.helper

import com.oyetech.wallpaperList.ui.richSearch.RichSearchFragment

/**
Created by Erdi Özbek
-18.02.2024-
-18:59-
 **/

fun RichSearchFragment.prepareColorTypeProperty() {
    var colorSelectionLayout = binding.csColorSelection
    colorSelectionLayout.repository = this

}