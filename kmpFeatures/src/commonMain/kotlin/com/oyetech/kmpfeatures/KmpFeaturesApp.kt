package com.oyetech.kmpfeatures

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.kmpfeatures.di.KmpFeaturesKoin
import com.oyetech.kmpfeatures.di.platformKoinModule
import com.oyetech.kmpfeatures.navigation.KmpFeaturesRoutes
import com.oyetech.kmpfeatures.navigation.kmpFeaturesNavGraph
import com.oyetech.kmpmodels.navigation.RouteKMP
import com.oyetech.viewmodule.ViewModuleTheme
import org.koin.compose.KoinApplication

@Composable
fun KmpFeaturesApp() {
    KoinApplication(application = { modules(KmpFeaturesKoin.module, platformKoinModule) }) {
        val navController = rememberNavController()

        ViewModuleTheme(darkTheme = false) {
            NavHost(
                navController = navController,
                startDestination = KmpFeaturesRoutes.path(RouteKMP.Home),
            ) {
                kmpFeaturesNavGraph(navController)
            }
        }
    }
}
