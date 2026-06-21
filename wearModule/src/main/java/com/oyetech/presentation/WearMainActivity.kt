/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.oyetech.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.watchAppFeatures.wearAppNavigation
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class WearMainActivity : ComponentActivity() {

    private val navigationUseCase: NavigationUseCase by KoinJavaComponent.inject(
        NavigationUseCase::class.java
    )

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Timber.d("WearMainActivity: POST_NOTIFICATIONS granted=$granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        setContent {
            val backStack = rememberNavBackStack(AppRoute.StopwatchDurationScreen)
            val coroutineScope = rememberCoroutineScope()

            SideEffect {
                navigationUseCase.setNavigator(
                    navigateTo = { route ->
                        coroutineScope.launch { backStack.add(route as NavKey) }
                    },
                    goBack = {
                        coroutineScope.launch {
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        }
                    }
                )
            }

            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    wearAppNavigation()
                }
            )
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            Timber.d("WearMainActivity: POST_NOTIFICATIONS already granted=$granted")
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
