package com.oyetech.kmpfeatures.home

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.navigation.RouteKMP
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.HomeAction
import com.oyetech.kmpmodels.ui.state.HomeUiState
import kotlinx.coroutines.CoroutineScope

class HomeOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val googleLoginOperation: GoogleLoginOperation,
) : BaseFeatureOperator<HomeUiState, HomeAction, Nothing>(
    initialState = HomeUiState(
        welcomeMessage = StringKeys.kmpFeaturesReady,
        showGoogleLogin = googleLoginOperation.supportsSessions,
    ),
    operatorScope = operatorScope,
) {
    init {
        if (googleLoginOperation.supportsSessions) {
            launch {
                googleLoginOperation.session.collect { session ->
                    updateState {
                        copy(
                            isError = session.isError,
                            errorMessage = session.errorMessage,
                            canRetrySession = session.isError,
                        )
                    }
                }
            }
        }
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.GoogleLoginClicked -> navigationUseCase.navigateTo(RouteKMP.Login)
            HomeAction.RetrySessionClicked -> launch {
                googleLoginOperation.startSession()
            }

            HomeAction.AdminLoginClicked -> {
                if (googleLoginOperation.supportsSessions) {
                    val previousSessionKey = googleLoginOperation.sessionKey
                    launch {
                        googleLoginOperation.selectAdmin().fold(
                            onSuccess = {
                                if (googleLoginOperation.sessionKey == previousSessionKey) {
                                    navigationUseCase.navigateTo(RouteKMP.Diary)
                                }
                            },
                            onFailure = {
                                updateState {
                                    copy(
                                        isError = true,
                                        errorMessage = StringKeys.sessionUnavailable
                                    )
                                }
                            },
                        )
                    }
                } else {
                    navigationUseCase.navigateTo(RouteKMP.Diary)
                }
            }

            HomeAction.OperatorExampleClicked -> {
                navigationUseCase.navigateTo(RouteKMP.OperatorExample)
            }

            HomeAction.ErrorDismissed -> updateState { copy(isError = false, errorMessage = "") }
        }
    }
}
