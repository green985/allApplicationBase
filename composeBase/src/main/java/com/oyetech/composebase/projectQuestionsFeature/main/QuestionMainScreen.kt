package com.oyetech.composebase.projectQuestionsFeature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.projectQuestionsFeature.generalOperationScreen.GeneralOperationScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppBottomNavigationView
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuestionsFeature.navigation.questionAppNavigation
import com.oyetech.composebase.projectRadioFeature.theme.RadioAppTheme
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator.navHostScreenSetup
import com.oyetech.domain.useCases.NavigationUseCase

/**
Created by Erdi Özbek
-1.10.2025-
-15:16-
 **/

@Composable
fun QuestionMainScreen(
    navigationUseCase: NavigationUseCase,
) {
    RadioAppTheme {
        val navController = rememberNavController()
        navigationUseCase.setNavigator { action ->
            navController.navigate(action)
        }
        GeneralOperationScreenSetup({
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = QuestionAppProjectRoutes.questionApplicationBottomTabNavList.first().path,
                    ) {
                        navHostScreenSetup(navController, navigationUseCase)

                        questionAppNavigation(navController)
                    }
                }
                QuestionAppBottomNavigationView(navController = navController)
            }
        })

    }
}

@Composable
fun QuestionAppDebugRoot(navigationUseCase: NavigationUseCase) {
    val navController = rememberNavController()
    // Hook NavigationUseCase to Compose navController in debug as well
    navigationUseCase.setNavigator { action ->
        navController.navigate(action)
    }
    NavHost(
        navController = navController,
        startDestination = AllScreenNavigator.startApp,
    ) {
        navHostScreenSetup(navController, navigationUseCase)
        // Expose Question app routes alongside shared ones in debug
        questionAppNavigation(navController)
    }
}