package com.oyetech.kmpfeatures.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.oyetech.kmpfeatures.daily.DailyPagerScreenSetup
import com.oyetech.kmpfeatures.diary.DiaryScreenSetup
import com.oyetech.kmpfeatures.home.HomeScreenSetup
import com.oyetech.kmpfeatures.login.LoginScreenSetup

fun androidx.navigation.NavGraphBuilder.kmpFeaturesNavGraph(
    navController: NavHostController,
) {
    composable(KmpFeaturesRoutes.Home) {
        HomeScreenSetup(
            onLoginClick = {
                navController.navigate(KmpFeaturesRoutes.Login)
            },
        )
    }
    composable(KmpFeaturesRoutes.Login) {
        LoginScreenSetup(
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
        DailyPagerScreenSetup(
            onBackClick = navController::popBackStack,
        )
    }
    composable(KmpFeaturesRoutes.Diary) {
        DiaryScreenSetup(
            onBackClick = navController::popBackStack,
        )
    }
}
