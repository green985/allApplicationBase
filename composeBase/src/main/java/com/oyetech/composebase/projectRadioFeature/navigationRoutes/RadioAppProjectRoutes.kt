package com.oyetech.composebase.projectRadioFeature.navigationRoutes

import com.oyetech.composebase.R
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationItem
import com.oyetech.composebase.navigator.Route

/**
Created by Erdi Özbek
-23.10.2024-
-18:53-
 **/

object RadioAppProjectRoutes {
    val TimerDialog: Route = Route("timerDialog")
    val TabRadioAllList = Route("tab/radioAllList")
    val RadioList = Route("radioList")
    val RadioSearchList = Route("radioSearchList")
    val TabFav = Route("radioList/TabFav")
    val TabHistory = Route("radioList/TabHistory")
    val TabCategories = Route("radioList/TabCategories")
    val TabSettings = Route("radioList/TabSettings")
    val ContactScreen = Route("radioList/ContactScreen")

    val radioApplicationBottomTabNavList = listOf(
        RadioAppNavItem.TabRadioAllList,
        RadioAppNavItem.TabFav,
        RadioAppNavItem.TabHistory,
        RadioAppNavItem.TabCategories,
        RadioAppNavItem.SettingsTab,
    )

}

sealed class RadioAppNavItem {
    object TabRadioAllList :
        BottomNavigationItem(
            path = RadioAppProjectRoutes.TabRadioAllList.route.toString(),
            title = R.string.nav_item_stations,
            icon = R.drawable.ic_tab_all_radio
        )

    object TabFav :
        BottomNavigationItem(
            path = RadioAppProjectRoutes.TabFav.route.toString(),
            title = R.string.nav_item_starred,
            icon = R.drawable.ic_favorite_border
        )

    object TabHistory :
        BottomNavigationItem(
            path = RadioAppProjectRoutes.TabHistory.route.toString(),
            title = R.string.nav_item_history,
            icon = R.drawable.ic_history
        )

    object TabCategories :
        BottomNavigationItem(
            path = RadioAppProjectRoutes.TabCategories.route.toString(),
            title = R.string.detail_tags,
            icon = R.drawable.ic_categories
        )

    object SettingsTab :
        BottomNavigationItem(
            path = RadioAppProjectRoutes.TabSettings.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

}