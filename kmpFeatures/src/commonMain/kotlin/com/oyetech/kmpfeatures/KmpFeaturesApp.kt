package com.oyetech.kmpfeatures

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import com.oyetech.kmpfeatures.di.KmpFeaturesKoin
import com.oyetech.kmpfeatures.di.platformKoinModule
import com.oyetech.kmpfeatures.navigation.KmpFeaturesRoutes
import com.oyetech.kmpfeatures.navigation.kmpFeaturesNavGraph
import com.oyetech.kmpmodels.navigation.RouteKMP
import com.oyetech.viewmodule.ViewModuleTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun KmpFeaturesApp() {
    KoinApplication(application = { modules(KmpFeaturesKoin.module, platformKoinModule) }) {
        val googleLoginOperation = koinInject<GoogleLoginOperation>()
        val session by googleLoginOperation.session.collectAsState()
        val sessionSnackbar = koinInject<SnackbarDelegate>()
        LaunchedEffect(googleLoginOperation) {
            googleLoginOperation.startSession()
        }
        DisposableEffect(googleLoginOperation) {
            onDispose(googleLoginOperation::stopSession)
        }
        LaunchedEffect(session.errorMessage) {
            if (session.errorMessage.isNotEmpty()) {
                sessionSnackbar.triggerSnackbarState(message = session.errorMessage)
            }
        }
        if (session.isLoading) {
            ViewModuleTheme(darkTheme = false) {
                CircularProgressIndicator()
            }
            return@KoinApplication
        }
        key(googleLoginOperation.sessionKey) {
            val navController = rememberNavController()
            val navigationUseCase = koinInject<NavigationUseCase>()
            val snackbarDelegate = koinInject<SnackbarDelegate>()
            val snackbarHostState = remember { SnackbarHostState() }

            DisposableEffect(navController, navigationUseCase) {
                navigationUseCase.setNavigator(
                    navigateTo = { route ->
                        require(route is RouteKMP)
                        navController.navigate(KmpFeaturesRoutes.path(route))
                    },
                    goBack = {
                        if (!navController.popBackStack() &&
                            googleLoginOperation.supportsSessions
                        ) {
                            navController.navigate(KmpFeaturesRoutes.path(RouteKMP.Home))
                        }
                    },
                )
                onDispose(navigationUseCase::clearNavigator)
            }

            LaunchedEffect(snackbarDelegate, snackbarHostState) {
                snackbarDelegate.snackbarUiState.collectLatest { state ->
                    if (state.message.isNotEmpty()) {
                        val result = snackbarHostState.showSnackbar(
                            message = state.message,
                            actionLabel = state.actionLabel,
                        )
                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            state.onAction?.invoke()
                        }
                    }
                }
            }

            ViewModuleTheme(darkTheme = false) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { contentPadding ->
                    NavHost(
                        modifier = androidx.compose.ui.Modifier.padding(contentPadding),
                        navController = navController,
                        startDestination = KmpFeaturesRoutes.path(
                            when {
                                session.isAuthenticated -> RouteKMP.Diary
                                session.requiresLogin -> RouteKMP.Login
                                else -> RouteKMP.Home
                            },
                        ),
                    ) {
                        kmpFeaturesNavGraph(
                            canOpenDiary = !googleLoginOperation.supportsSessions || session.isAuthenticated,
                        )
                    }
                }
            }
        }
    }
}
