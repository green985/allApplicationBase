package com.oyetech.composebase.projectQuestionsFeature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppBottomNavigationView
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuestionsFeature.navigation.questionAppNavigation
import com.oyetech.composebase.projectRadioFeature.theme.RadioAppTheme
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator.navHostScreenSetup
import com.oyetech.domain.useCases.NavigationUseCase

/**
Created by Erdi Özbek
-1.10.2025-
-15:16-
 **/

@Composable
fun QuestionMainScreen(
    navHostController: NavHostController,
    navigationUseCase: NavigationUseCase,
) {
    RadioAppTheme {
        val navController = rememberNavController()
        navigationUseCase.setNavigator { action ->
            navController.navigate(action)
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = androidx.compose.ui.Modifier.fillMaxSize()
        ) {
            Column(
                modifier = androidx.compose.ui.Modifier
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
    }
}