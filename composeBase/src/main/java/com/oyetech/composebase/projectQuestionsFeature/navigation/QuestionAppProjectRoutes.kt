package com.oyetech.composebase.projectQuestionsFeature.navigation

import com.oyetech.composebase.R
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationItem
import com.oyetech.composebase.navigator.Route
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppNavItem.QuestionAppMessageTab
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppNavItem.QuestionAppSettingsTab
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppNavItem.QuestionAppUserListTab
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppNavItem.TabQuestionAppHomepage
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.MessageConversationList
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.QuestionAppHomepage
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.QuestionAppSettings
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.UserList
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Warp Agent
-01.10.2025-
-For QuestionApp-
 **/

object QuestionAppProjectRoutes {

    val QuestionCreateQuestionPage = Route("questionCreateQuestionPage")
    val QuestionAppHomepage = Route("questionAppHomepage")
    val QuestionAppSettings = Route("question/QuestionAppSettings")
    val MessageConversationList = Route("question/MessageConversationList")
    val UserList = Route("question/UserList")

    val questionApplicationBottomTabNavList = listOf(
        TabQuestionAppHomepage,
        QuestionAppSettingsTab,
        QuestionAppUserListTab,
        QuestionAppMessageTab,
    )
}

sealed class QuestionAppNavItem {
    object TabQuestionAppHomepage :
        BottomNavigationItem(
            path = QuestionAppHomepage.route.toString(),
            titleText = LanguageKey.home,
            icon = R.drawable.ic_tab_all_radio
        )

    object QuestionAppSettingsTab :
        BottomNavigationItem(
            path = QuestionAppSettings.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

    object QuestionAppMessageTab :
        BottomNavigationItem(
            path = MessageConversationList.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

    object QuestionAppUserListTab :
        BottomNavigationItem(
            path = UserList.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )
}
