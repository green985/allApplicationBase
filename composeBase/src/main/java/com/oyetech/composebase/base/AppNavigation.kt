package com.oyetech.composebase.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationBar
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.radioAppNavigation
import com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.RadioPlayerSetup

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioPlayerSetup() {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .background(Color.Transparent)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                    ) {
                        radioAppNavigation(navController)

                    }

                }
            }

        }
        Row(modifier = Modifier.fillMaxWidth()) {

            BottomNavigationBar(
                navController,
                navItems = RadioAppProjectRoutes.radioApplicationBottomTabNavList
            )
        }
    }

}
