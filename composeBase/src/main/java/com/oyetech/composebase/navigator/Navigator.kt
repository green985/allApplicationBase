package com.oyetech.composebase.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController

class Navigator(val navController: NavHostController) {

    // Navigate to a destination
    fun navigateTo(route: String) {
        if (route.isEmpty()) return
        navController.navigate(route)
    }

    // Navigate with arguments
    fun navigateTo(route: String, args: String) {
        navController.navigate("$route/$args")
    }

    // Go back to the previous screen
    fun goBack() {
        navController.popBackStack()
    }

    // Navigate to a deep link
    fun navigateToDeepLink(deepLink: String) {
        navController.navigate(deepLink)
    }

    // Clear back stack and navigate to new screen
    fun navigateAndClearStack(route: String) {
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
        }
    }

    // Navigate with optional lambda for customizing the navigation options
    fun navigateWithCustomOptions(route: String, builder: NavOptionsBuilder.() -> Unit = {}) {
        navController.navigate(route, builder)
    }
}

@Composable
fun rememberNavigator(navController: NavHostController = rememberNavController()): Navigator {
    return remember(navController) {
        Navigator(navController)
    }
}
