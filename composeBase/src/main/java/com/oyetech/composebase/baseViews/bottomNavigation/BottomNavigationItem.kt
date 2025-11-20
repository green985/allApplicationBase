package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

open class BottomNavigationItem(
    val path: String,
    @param:StringRes val title: Int = 0,
    val titleText: String = "",
    @param:DrawableRes val icon: Int,
)