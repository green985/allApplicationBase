package com.oyetech.kmpfeatures.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.oyetech.kmpfeatures.diary.DiaryScreen
import com.oyetech.kmpfeatures.home.HomeScreen
import com.oyetech.kmpfeatures.login.LoginScreen
import com.oyetech.kmpfeatures.daily.DailyPagerScreen

fun androidx.navigation.NavGraphBuilder.kmpFeaturesNavGraph(
    navController: NavHostController,
) {
    composable(KmpFeaturesRoutes.Home) {
        HomeScreen(
            onLoginClick = {
                navController.navigate(KmpFeaturesRoutes.Login)
            },
        )
    }
    composable(KmpFeaturesRoutes.Login) {
        LoginScreen(
            onBackClick = navController::popBackStack,
            onLoginSuccess = {
                navController.navigate(KmpFeaturesRoutes.Diary) {
                    popUpTo(KmpFeaturesRoutes.Login) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )
    }
    composable(KmpFeaturesRoutes.DailyPager) {
        DailyPagerScreen(
            onBackClick = navController::popBackStack,
        )
    }
    composable(KmpFeaturesRoutes.Diary) {
        DiaryScreen(
            onBackClick = navController::popBackStack,
        )
    }
}
