package com.oyetech.kmpfeatures.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.oyetech.kmpfeatures.daily.DailyPagerScreenSetup
import com.oyetech.kmpfeatures.diary.DiaryScreenSetup
import com.oyetech.kmpfeatures.example.OperatorExampleScreenSetup
import com.oyetech.kmpfeatures.home.HomeScreenSetup
import com.oyetech.kmpfeatures.login.LoginScreenSetup
import com.oyetech.kmpmodels.navigation.RouteKMP

fun androidx.navigation.NavGraphBuilder.kmpFeaturesNavGraph(
    navController: NavHostController,
) {
    composable(KmpFeaturesRoutes.path(RouteKMP.Home)) {
        HomeScreenSetup(
            onLoginClick = {
                navController.navigate(KmpFeaturesRoutes.path(RouteKMP.Login))
            },
            onOperatorExampleClick = {
                navController.navigate(KmpFeaturesRoutes.path(RouteKMP.OperatorExample))
            },
        )
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.Login)) {
        LoginScreenSetup(
            onBackClick = navController::popBackStack,
            onLoginSuccess = {
                navController.navigate(KmpFeaturesRoutes.path(RouteKMP.Diary)) {
                    popUpTo(KmpFeaturesRoutes.path(RouteKMP.Login)) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.DailyPager)) {
        DailyPagerScreenSetup(
            onBackClick = navController::popBackStack,
        )
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.Diary)) {
        DiaryScreenSetup(
            onBackClick = navController::popBackStack,
        )
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.OperatorExample)) {
        OperatorExampleScreenSetup(
            onBackClick = navController::popBackStack,
        )
    }
}
