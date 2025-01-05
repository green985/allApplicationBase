package com.oyetech.composebase.projectRadioFeature.main

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
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
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppNavigationWrapperWithPlayerSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationScreenSetup
import com.oyetech.composebase.projectRadioFeature.theme.RadioAppTheme
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils
import org.koin.java.KoinJavaComponent

/**
Created by Erdi Özbek
-15.12.2024-
-00:15-
 **/

class RadioMainActivity : ComponentActivity() {

    val loginOperationRepository: GoogleLoginRepository by KoinJavaComponent.inject(
        GoogleLoginRepository::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerServiceUtils.startService()
        enableEdgeToEdge()
        setContent {

            RadioAppTheme {
                val navController = rememberNavController()

                GeneralOperationScreenSetup(
                    content =
                    {
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            RadioAppNavigationWrapperWithPlayerSetup(
                                navController = navController,
                                startDestination = RadioAppProjectRoutes.TabRadioAllList.route
                            )

                        }
                    }, navController = navController
                )
            }

        }

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check whether the initial data is ready.
                    return if (loginOperationRepository.userAutoLoginStateFlow.value) {
                        // The content is ready. Start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready. Suspend.
                        false
                    }
                }
            }
        )
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
        val navigator = rememberNavController()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navigator,
                    navItems = RadioAppProjectRoutes.radioApplicationBottomTabNavList
                )
            },

            content = {
                //AppNavigation(navigator = navigator)
            })

    }
}