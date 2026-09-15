package com.oyetech.kmpfeatures.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.oyetech.kmpfeatures.home.HomeScreen

fun androidx.navigation.NavGraphBuilder.kmpFeaturesNavGraph(
    navController: NavHostController,
) {
    composable(KmpFeaturesRoutes.Home) {
        HomeScreen(
            onNavigate = navController::navigate,
        )
    }
}
