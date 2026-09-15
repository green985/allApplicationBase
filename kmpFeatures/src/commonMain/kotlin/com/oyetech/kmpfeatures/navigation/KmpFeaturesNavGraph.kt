package com.oyetech.kmpfeatures.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.oyetech.kmpfeatures.home.HomeScreen
import com.oyetech.kmpfeatures.login.LoginScreen

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
        )
    }
}
