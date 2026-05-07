package com.oyetech.composebase.projectQuestionsFeature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.oyetech.composebase.helpers.eventNavigator.TestEventNavigator
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.projectQuestionsFeature.generalOperationScreen.GeneralOperationScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectBottomNavigationDestinations
import com.oyetech.composebase.projectQuestionsFeature.theme.RadioAppTheme
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.debug.DebugUnlockHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent
import timber.log.Timber

class QuestionMainActivity : ComponentActivity() {

    // Initialize ViewModel - this will set viewModelScope in ViewModelScopeProviderUseCase
    private val mainActivityVm: QuestionMainActivityVm by viewModel()

    private val testEventNavigator: TestEventNavigator by KoinJavaComponent.inject(
        TestEventNavigator::class.java
    )
    private val navigationUseCase: NavigationUseCase by KoinJavaComponent.inject(NavigationUseCase::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugUnlockHelper.unlockForDebug(this)
        enableEdgeToEdge()

        setContent {
            RadioAppTheme {
                val startDestination =
                    QuestionAppProjectBottomNavigationDestinations.questionApplicationBottomTabNavList
                        .first().route
                val backStack = rememberNavBackStack(startDestination)
                val coroutineScope = rememberCoroutineScope()

                // SideEffect ensures the lambdas are always fresh after recomposition
                // without triggering a new composition cycle.
                SideEffect {
                    navigationUseCase.setNavigator(
                        navigateTo = { route ->
                            coroutineScope.launch { backStack.add(route as NavKey) }
                        },
                        goBack = {
                            coroutineScope.launch {
                                Timber.d("size of backstack before pop: ${backStack.size}")
                                if (backStack.size > 1) backStack.removeLastOrNull()
                            }
                        }
                    )
                }

                GeneralOperationScreenSetup({
                    if (GeneralSettings.isDebug()) {
                        QuestionMainScreen(
                            backStack = backStack,
                            navigationUseCase = navigationUseCase
                        )
                    } else {
                        QuestionMainScreen(
                            backStack = backStack,
                            navigationUseCase = navigationUseCase
                        )
                    }
                })
            }
        }

        // Optional test event hook (kept minimal; no permissions here)
        lifecycleScope.launch(Dispatchers.IO) {
            // testEventNavigator.triggerTestEvents(...) // Add if needed during debugging
        }
    }
}
