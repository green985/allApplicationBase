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
object QuestionAppProjectRoutes {
    val QuestionCreateQuestionPage = AppRoute.QuestionCreateQuestionPage()
    val QuestionAppHomepage = AppRoute.QuestionAppHomepage
    val QuestionAppSettings = AppRoute.QuestionAppSettings
    val AdminApproveQuestion = AppRoute.AdminApproveQuestion
    val QuestionListWithParams = AppRoute.QuestionListWithParams()
    val QuestionPager = AppRoute.QuestionPager
    val UserProfile = AppRoute.UserProfile()
    val CompleteProfileScreen = AppRoute.CompleteProfileScreen
    val EditProfile = AppRoute.EditProfile
    val QuestionFormScreen = AppRoute.QuestionFormScreen()
    val UserList = AppRoute.UserList
    val MessageDetail = AppRoute.MessageDetail()
    val MessageConversationList = AppRoute.MessageConversationList
}

object QuestionAppProjectBottomNavigationDestinations {
    val questionApplicationBottomTabNavList = listOf(
        QuestionAppNavItem.TabQuestionAppHomepage,
        QuestionAppNavItem.TabQuestionList,
        QuestionAppNavItem.QuestionAppSettingsTab,
        QuestionAppNavItem.UserProfileTab,
        QuestionAppNavItem.QuestionAppMessageTab,
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

    object QuestionAppMessageTab :
        BottomNavigationItem(
            route = AppRoute.MessageConversationList,
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

    object UserProfileTab :
        BottomNavigationItem(
            route = AppRoute.UserProfile(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )
}
