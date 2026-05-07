package com.oyetech.composebase.projectQuestionsFeature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
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

/**
Created by Erdi Özbek
-1.10.2025-
-15:16-
 **/

/**
 * Pure UI host for the Question app. Owns no navigation state — the caller
 * is responsible for creating [backStack] and wiring [NavigationUseCase].
 */
@Composable
fun QuestionMainScreen(
    backStack: NavBackStack<NavKey>,
    navigationUseCase: NavigationUseCase,
) {
    RadioAppTheme {
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

/**
 * Self-contained root for the Question app — creates its own [NavBackStack]
 * and wires [NavigationUseCase] via [SideEffect].
 *
 * Use this when embedding the Question app as a nested navigation host
 * (e.g. inside [AppRoute.QuestionAppStart] entry).
 */
@Composable
fun QuestionAppNestedRoot(navigationUseCase: NavigationUseCase) {
    val startDestination =
        QuestionAppProjectBottomNavigationDestinations.questionApplicationBottomTabNavList
            .first().route
    val backStack = rememberNavBackStack(startDestination)
    val coroutineScope = rememberCoroutineScope()

    SideEffect {
        navigationUseCase.setNavigator(
            navigateTo = { route -> coroutineScope.launch { backStack.add(route as NavKey) } },
            goBack = {
                coroutineScope.launch {
                    if (backStack.size > 1) backStack.removeLastOrNull()
                }
            }
        )
    }

    QuestionMainScreen(backStack = backStack, navigationUseCase = navigationUseCase)
}

@Composable
fun QuestionAppDebugRoot(navigationUseCase: NavigationUseCase) {
    val backStack = rememberNavBackStack(AppRoute.AppFullApp)
    val coroutineScope = rememberCoroutineScope()

    SideEffect {
        navigationUseCase.setNavigator(
            navigateTo = { route -> coroutineScope.launch { backStack.add(route as NavKey) } },
            goBack = { coroutineScope.launch { if (backStack.size > 1) backStack.removeLastOrNull() } }
        )
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            navHostScreenSetup(navigationUseCase)
            questionAppNavigation()
        }
    )
}
