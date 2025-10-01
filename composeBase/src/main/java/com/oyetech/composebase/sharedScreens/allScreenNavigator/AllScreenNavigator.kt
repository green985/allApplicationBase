package com.oyetech.composebase.sharedScreens.allScreenNavigator

import QuoteAppProjectRoutes
import QuoteBottomNavigationView
import RadioAppNavigationWrapperWithPlayerSetup
import RadioAppProjectRoutes
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
import com.oyetech.composebase.projectQuestionsFeature.main.QuestionMainScreen
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationScreenSetup
import com.oyetech.domain.useCases.NavigationUseCase

/**
Created by Erdi Özbek
-18.01.2025-
-11:10-
 **/

object AllScreenNavigator {

    const val startApp = "appFullApp"
    const val questionAppStart = "questionAppStart"

    val generalListOfScreen = emptyList<String>()

    fun NavGraphBuilder.navHostScreenSetup(
        navHostController: NavHostController,
        navigationUseCase: NavigationUseCase,
    ) {
        composable(startApp) {
            AllScreenNavigatorScreenSetup(
            )
        }

        composable(questionAppStart) {
            QuestionMainScreen(
                navigationUseCase
            )
        }

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
                    }
            )
        }
            val navHostControllerQuote = rememberNavController()
            LaunchedEffect(Unit) {
                navigationUseCase.setNavigator { action ->
                    navHostControllerQuote.navigate(action)
                }
            }
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
                                    startDestination = QuoteAppProjectRoutes.quoteApplicationBottomTabNavList.first().path,
                                ) {
//                                radioAppNavigation(navHostControllerQuote)
                                    quotesAppNavigation(navHostControllerQuote)
                                }
                            }
                            QuoteBottomNavigationView(
                                navController = navHostControllerQuote
                            )
                        }
                    }
            )
        }
    }
}
