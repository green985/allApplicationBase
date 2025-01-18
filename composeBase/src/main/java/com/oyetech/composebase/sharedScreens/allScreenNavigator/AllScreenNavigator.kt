package com.oyetech.composebase.sharedScreens.allScreenNavigator

import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes

/**
Created by Erdi Özbek
-18.01.2025-
-11:10-
 **/

object AllScreenNavigator {

    val generalListOfScreen = listOf(
        // Radio App Routes
        RadioAppProjectRoutes.TimerDialog.route,
        RadioAppProjectRoutes.TabRadioAllList.route,
        RadioAppProjectRoutes.RadioList.route,
        RadioAppProjectRoutes.RadioSearchList.route,
        RadioAppProjectRoutes.TabFav.route,
        RadioAppProjectRoutes.TabHistory.route,
        RadioAppProjectRoutes.TabCategories.route,
        RadioAppProjectRoutes.TabSettings.route,
        RadioAppProjectRoutes.ContactScreen.route,
        RadioAppProjectRoutes.QuotesListScreen.route,
        RadioAppProjectRoutes.CommentScreen.route,
        RadioAppProjectRoutes.CommentScreenWithContentId.route,
        RadioAppProjectRoutes.LoginOperationScreen.route,
        RadioAppProjectRoutes.CompleteProfileScreen.route,

        // Quote App Routes
        QuoteAppProjectRoutes.QuoteHomeRoute.route,
        QuoteAppProjectRoutes.QuoteDetailRoute.route,
        QuoteAppProjectRoutes.QuoteAuthorList.route
    )


}