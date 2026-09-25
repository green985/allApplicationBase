package com.oyetech.kmpfeatures

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.kmpfeatures.di.KmpFeaturesKoin
import com.oyetech.kmpfeatures.navigation.KmpFeaturesRoutes
import com.oyetech.kmpfeatures.navigation.kmpFeaturesNavGraph
import com.oyetech.kmpfeatures.theme.KmpFeaturesTheme
import org.koin.compose.KoinApplication

@Composable
fun KmpFeaturesApp() {
    KoinApplication(application = { modules(KmpFeaturesKoin.module) }) {
        val navController = rememberNavController()

        KmpFeaturesTheme {
            NavHost(
                navController = navController,
                startDestination = KmpFeaturesRoutes.Home,
            ) {
                kmpFeaturesNavGraph(navController)
            }
        }
    }
}
