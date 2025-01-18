package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppNavigationWrapperWithPlayerSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationScreenSetup

/**
Created by Erdi Özbek
-18.01.2025-
-11:10-
 **/

object AllScreenNavigator {

    val startApp = "appFullApp"
    val radioStart = "radioStart"

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

    fun NavGraphBuilder.navHostScreenSetup(navHostController: NavHostController) {
        composable(AllScreenNavigator.startApp) {
            AllScreenNavigatorScreenSetup(
                navigationRoute = { route ->
                    navHostController.navigate(route)
                },
            )
        }

        // TabRadioAllList Route
        composable(AllScreenNavigator.radioStart) {
            val navHostControllerRadio = rememberNavController()
            GeneralOperationScreenSetup(
                content =
                {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        RadioAppNavigationWrapperWithPlayerSetup(
                            navController = navHostControllerRadio,
                            startDestination = RadioAppProjectRoutes.TabRadioAllList.route
                        )

                    }
                }, navController = navHostControllerRadio
            )
        }
    }
}
