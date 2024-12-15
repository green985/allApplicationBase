package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

open class BottomNavigationItem(
    val path: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
)