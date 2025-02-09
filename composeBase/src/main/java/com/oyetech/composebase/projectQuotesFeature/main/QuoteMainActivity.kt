package com.oyetech.composebase.projectQuotesFeature.main

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationBar
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.projectQuotesFeature.navigation.quotesAppNavigation
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.theme.RadioAppTheme
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator.navHostScreenSetup
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.languageModule.keyset.LanguageKey.errorText
import org.koin.java.KoinJavaComponent
import timber.log.Timber

/**
Created by Erdi Özbek
-25.01.2025-
-13:49-
 **/

class QuoteMainActivity : ComponentActivity() {

    val loginOperationRepository: GoogleLoginRepository by KoinJavaComponent.inject(
        GoogleLoginRepository::class.java
    )

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (GeneralSettings.isDebug()) {

                RadioAppTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = AllScreenNavigator.startApp,
                    ) {
                        navHostScreenSetup(navController)
                        quotesAppNavigation(navController)
                    }
                }
            } else {
                RadioAppTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = AllScreenNavigator.quoteStart,
                    ) {
                        navHostScreenSetup(navController)
                        quotesAppNavigation(navController)
                    }
                }
            }

            Timber.d("onCreate Error texttttt: $errorText")

            // todo will be check later for auto login things looks like a block general navigator screen
            val content: View = findViewById(android.R.id.content)
            content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true

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

    }

    @Composable
    @Preview
    fun GreetingVibrationPreview() {
        RadioAppTheme {
            val navigator = rememberNavController()

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        navController = navigator,
                        navItems = RadioAppProjectRoutes.radioApplicationBottomTabNavList
                    )
                },

                content = {
                    //AppNavigation(navigator = navigator)
                })

        }
    }
}