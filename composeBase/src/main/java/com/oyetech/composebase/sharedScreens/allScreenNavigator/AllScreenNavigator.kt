package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.composebase.projectQuestionsFeature.main.QuestionAppNestedRoot
import com.oyetech.domain.useCases.NavigationUseCase

/**
Created by Erdi Özbek
-18.01.2025-
-11:10-
 **/

object AllScreenNavigator {

    /** Routes displayed in the debug launcher grid. */
    val generalListOfScreen: List<AppRoute> = listOf(
        AppRoute.QuestionCreateQuestionPage(),
        AppRoute.QuestionPager,
        AppRoute.AdminApproveQuestion,
    )

    fun EntryProviderScope<NavKey>.navHostScreenSetup(
        navigationUseCase: NavigationUseCase,
    ) {
        entry<AppRoute.AppFullApp> {
            AllScreenNavigatorScreenSetup()
        }

        entry<AppRoute.QuestionAppStart> {
            QuestionAppNestedRoot(navigationUseCase)
        }
    }
}
