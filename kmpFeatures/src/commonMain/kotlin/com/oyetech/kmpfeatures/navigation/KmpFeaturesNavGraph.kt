package com.oyetech.kmpfeatures.navigation

import androidx.compose.runtime.Composable
import com.oyetech.kmpfeatures.home.HomeScreen

@Composable
fun KmpFeaturesNavGraph(
    route: String,
    onNavigate: (String) -> Unit,
) {
    when (route) {
        KmpFeaturesRoutes.Home -> {
            HomeScreen(
                onNavigate = onNavigate,
            )
        }
    }
}
