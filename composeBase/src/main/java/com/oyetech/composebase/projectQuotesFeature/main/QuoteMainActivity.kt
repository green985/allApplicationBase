package com.oyetech.composebase.projectQuotesFeature.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
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

    val TAG = "QuoteMainActivity"

    val loginOperationRepository: GoogleLoginRepository by KoinJavaComponent.inject(
        GoogleLoginRepository::class.java
    )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
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






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = "fcm_default_channel"
            val channelName = "General"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.getString(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }
        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                Timber.d("Notification permission granted")
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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