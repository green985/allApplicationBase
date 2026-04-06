package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.oyetech.composebase.navigator.AppRoute

open class BottomNavigationItem(
    val route: AppRoute,
    @param:StringRes val title: Int = 0,
    val titleText: String = "",
    @param:DrawableRes val icon: Int,
)
