package com.oyetech.composebase.projectRadioFeature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationBar
import com.oyetech.composebase.navigator.rememberNavigator
import com.oyetech.composebase.projectRadioFeature.RadioAppNavigation
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationScreenSetup
import com.oyetech.composebase.projectRadioFeature.theme.RadioAppTheme
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils

/**
Created by Erdi Özbek
-15.12.2024-
-00:15-
 **/

class RadioMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerServiceUtils.startService()
        enableEdgeToEdge()
        setContent {

            RadioAppTheme {
                val navController = rememberNavController()

                GeneralOperationScreenSetup {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        RadioAppNavigation(
                            navController = navController,
                            startDestination = RadioAppProjectRoutes.TabRadioAllList.route
                        )
                    }
                }

            }

        }
    }

    override fun onResume() {
        super.onResume()
        PlayerServiceUtils.startService()
    }
}

@Composable
@Preview
fun GreetingVibrationPreview() {
    RadioAppTheme {
        val navigator = rememberNavigator()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navigator.navController,
                    navItems = RadioAppProjectRoutes.radioApplicationBottomTabNavList
                )
            },

            content = {
                //AppNavigation(navigator = navigator)
            })

    }
}