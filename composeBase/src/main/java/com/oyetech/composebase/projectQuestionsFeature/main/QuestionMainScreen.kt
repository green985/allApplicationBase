package com.oyetech.composebase.projectQuestionsFeature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppBottomNavigationView
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectBottomNavigationDestinations
import com.oyetech.composebase.projectQuestionsFeature.navigation.questionAppNavigation
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors
import com.oyetech.composebase.projectQuestionsFeature.theme.RadioAppTheme
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator.navHostScreenSetup
import com.oyetech.domain.useCases.NavigationUseCase
import kotlinx.coroutines.launch
import timber.log.Timber

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
        val startDestination =
            QuestionAppProjectBottomNavigationDestinations.questionApplicationBottomTabNavList
                .first().route
        val backStack = rememberNavBackStack(startDestination)
        val coroutineScope = rememberCoroutineScope()

        navigationUseCase.setNavigator(
            navigateTo = { route -> coroutineScope.launch { backStack.add(route as NavKey) } },
            goBack = {
                coroutineScope.launch {
                    Timber.d("size of backstack before pop: ${backStack.size}")
                    backStack.removeLastOrNull()
                }
            }
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(AppColors.background)
            ) {
                NavDisplay(
                    backStack = backStack,
                    entryProvider = entryProvider {
                        navHostScreenSetup(navigationUseCase)
                        questionAppNavigation()
                    }
                )
            }
            QuestionAppBottomNavigationView(backStack = backStack)
        }
    }
}

@Composable
fun QuestionAppDebugRoot(navigationUseCase: NavigationUseCase) {
    val backStack = rememberNavBackStack(AppRoute.AppFullApp)
    val coroutineScope = rememberCoroutineScope()

    navigationUseCase.setNavigator(
        navigateTo = { route -> coroutineScope.launch { backStack.add(route as NavKey) } },
        goBack = { coroutineScope.launch { backStack.removeLastOrNull() } }
    )

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            navHostScreenSetup(navigationUseCase)
            questionAppNavigation()
        }
    )
}
