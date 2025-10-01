package com.oyetech.composebase.projectQuestionsFeature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.oyetech.composebase.helpers.eventNavigator.TestEventNavigator
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.projectQuestionsFeature.theme.RadioAppTheme
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
            RadioAppTheme {
                if (GeneralSettings.isDebug()) {
                    QuestionAppDebugRoot(navigationUseCase)
                } else {

                    QuestionMainScreen(navigationUseCase)
                }
            }
        }

        // Optional test event hook (kept minimal; no permissions here)
        lifecycleScope.launch(Dispatchers.IO) {
            // testEventNavigator.triggerTestEvents(...) // Add if needed during debugging
        }
    }
}

