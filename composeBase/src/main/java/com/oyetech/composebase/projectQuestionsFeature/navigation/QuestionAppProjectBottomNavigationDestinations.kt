package com.oyetech.composebase.projectQuestionsFeature.navigation

import com.oyetech.composebase.R
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationItem
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Warp Agent
-01.10.2025-
-For QuestionApp-
 **/

/**
 * Canonical typed-route references for the Question app.
 * All screens use [AppRoute] subclasses — no string routes.
 */
object QuestionAppProjectBottomNavigationDestinations {
    val questionApplicationBottomTabNavList = listOf(
        QuestionAppNavItem.TabQuestionAppHomepage,
        QuestionAppNavItem.TabQuestionList,
        QuestionAppNavItem.QuestionAppSettingsTab,
        QuestionAppNavItem.UserProfileTab,
    )
}

sealed class QuestionAppNavItem {
    object TabQuestionAppHomepage :
        BottomNavigationItem(
            route = AppRoute.QuestionAppHomepage,
            titleText = LanguageKey.home,
            icon = R.drawable.ic_tab_all_radio
        )

    object TabQuestionList :
        BottomNavigationItem(
            route = AppRoute.QuestionPager,
            titleText = LanguageKey.home,
            icon = R.drawable.ic_tab_all_radio
        )

    object QuestionAppSettingsTab :
        BottomNavigationItem(
            route = AppRoute.QuestionAppSettings,
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

    object UserProfileTab :
        BottomNavigationItem(
            route = AppRoute.UserProfile(),
            titleText = LanguageKey.userProfile,
            icon = R.drawable.ic_tab_profile
        )
}
