package com.oyetech.composebase.projectQuestionsFeature.navigation

import com.oyetech.composebase.R
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationItem
import com.oyetech.composebase.navigator.Route
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.QuestionAppHomepage
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.QuestionAppSettings
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes.QuestionPager
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
    val AdminApproveQuestion = Route("question/AdminApproveQuestion")
    val QuestionListWithParams = Route("question/QuestionListWithParams")
    val QuestionPager = Route("question/QuestionPager")
    val UserProfile = Route("question/UserProfile")
    val CompleteProfileScreen = Route("question/CompleteProfileScreen")
    val EditProfile = Route("question/EditProfile")
    val QuestionFormScreen = Route("question/QuestionFormScreen")


    val UserList = Route("question/UserList")

    val MessageDetail = Route("question/MessageDetail")

    val MessageConversationList = Route("question/MessageConversationList")

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
            path = QuestionAppHomepage.route.toString(),
            titleText = LanguageKey.home,
            icon = R.drawable.ic_tab_all_radio
        )

    object TabQuestionList :
        BottomNavigationItem(
            path = QuestionPager.route.toString(),
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
            path = QuestionAppSettings.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

    object UserProfileTab :
        BottomNavigationItem(
            path = QuestionAppProjectRoutes.UserProfile.route
                .toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )
}
