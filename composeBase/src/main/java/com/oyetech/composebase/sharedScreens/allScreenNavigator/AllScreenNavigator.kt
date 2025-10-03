package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.oyetech.composebase.projectQuestionsFeature.main.QuestionMainScreen
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.domain.useCases.NavigationUseCase

/**
Created by Erdi Özbek
-18.01.2025-
-11:10-
 **/

object AllScreenNavigator {

    const val startApp = "appFullApp"
    const val questionAppStart = "questionAppStart"

    val generalListOfScreen = emptyList<String>().toMutableList<String>().apply {
        add(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
        add(QuestionAppProjectRoutes.QuestionList.route)
    }

    fun NavGraphBuilder.navHostScreenSetup(
        navHostController: NavHostController,
        navigationUseCase: NavigationUseCase,
    ) {
        composable(startApp) {
            AllScreenNavigatorScreenSetup()
        }

        composable(questionAppStart) {
            QuestionMainScreen(navigationUseCase)
        }
    }
}
