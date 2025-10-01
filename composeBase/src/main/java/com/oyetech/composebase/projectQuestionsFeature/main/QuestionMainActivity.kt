package com.oyetech.composebase.projectQuestionsFeature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.helpers.eventNavigator.TestEventNavigator
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.projectQuestionsFeature.navigation.questionAppNavigation
import com.oyetech.composebase.projectRadioFeature.theme.RadioAppTheme
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigator.navHostScreenSetup
import com.oyetech.domain.useCases.NavigationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class QuestionMainActivity : ComponentActivity() {

    private val testEventNavigator: TestEventNavigator by KoinJavaComponent.inject(
        TestEventNavigator::class.java
    )
    private val navigationUseCase: NavigationUseCase by KoinJavaComponent.inject(NavigationUseCase::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            if (GeneralSettings.isDebug()) {
                RadioAppTheme {
                    val navController = rememberNavController()
                    navigationUseCase.setNavigator { action ->
                        navController.navigate(action)
                    }
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = androidx.compose.ui.Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = androidx.compose.ui.Modifier
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = AllScreenNavigator.startApp,
                            ) {
                                navHostScreenSetup(navController, navigationUseCase)

                                questionAppNavigation(navController)
                            }
                        }
                    }
                }
            } else {
                RadioAppTheme {
                    val navController = rememberNavController()
                    navigationUseCase.setNavigator { action ->
                        navController.navigate(action)
                    }
                    QuestionMainScreen(navController, navigationUseCase)
                }
            }
        }

        // Optional test event hook (kept minimal; no permissions here)
        lifecycleScope.launch(Dispatchers.IO) {
            // testEventNavigator.triggerTestEvents(...) // Add if needed during debugging
        }
    }
}
