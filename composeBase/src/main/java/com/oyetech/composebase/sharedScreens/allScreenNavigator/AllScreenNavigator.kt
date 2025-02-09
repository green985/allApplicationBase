package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteBottomNavigationView
import com.oyetech.composebase.projectQuotesFeature.navigation.quotesAppNavigation
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppNavigationWrapperWithPlayerSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.radioAppNavigation
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationScreenSetup

/**
Created by Erdi Özbek
-18.01.2025-
-11:10-
 **/

object AllScreenNavigator {

    const val startApp = "appFullApp"
    const val radioStart = "radioStart"
    const val quoteStart = "quoteStart"

    val generalListOfScreen = listOf(

        // Quote App Routes
        QuoteAppProjectRoutes.AdviceQuoteDebug.route,
        QuoteAppProjectRoutes.QuoteAdviceScreen.route,
        QuoteAppProjectRoutes.QuoteSettings.route,
        QuoteAppProjectRoutes.QuoteAppHomepage.route,
        QuoteAppProjectRoutes.QuoteDetailRoute.route,
        QuoteAppProjectRoutes.QuoteAuthorList.route,

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
        RadioAppProjectRoutes.CommentScreenWithContentId.route,
        RadioAppProjectRoutes.LoginOperationScreen.route,
        RadioAppProjectRoutes.CompleteProfileScreen.route,

        )

    fun NavGraphBuilder.navHostScreenSetup(navHostController: NavHostController) {
        composable(startApp) {
            AllScreenNavigatorScreenSetup(
                navigationRoute = { route ->
                    navHostController.navigate(route)
                },
            )
        }

        // TabRadioAllList Route
        composable(radioStart) {
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
        // TabRadioAllList Route
        composable(quoteStart) {
            val navHostControllerQuote = rememberNavController()
            GeneralOperationScreenSetup(
                content =
                {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            NavHost(
                                navController = navHostControllerQuote,
                                startDestination = QuoteAppProjectRoutes.QuoteAppHomepage.route,
                            ) {
                                radioAppNavigation(navHostControllerQuote)
                                quotesAppNavigation(navHostControllerQuote)
                            }
                        }
                        QuoteBottomNavigationView(
                            navController = navHostControllerQuote
                        )
                    }
                }, navController = navHostControllerQuote
            )
        }
    }
}
