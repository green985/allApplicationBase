package com.oyetech.kmpfeatures.login

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import com.oyetech.kmpfeatures.auth.googleWebClientId
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.navigation.RouteKMP
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.LoginAction
import com.oyetech.kmpmodels.ui.state.LoginUiState
import kotlinx.coroutines.CoroutineScope

class LoginOperator(
    operatorScope: CoroutineScope,
    private val googleLoginOperation: GoogleLoginOperation,
    private val navigationUseCase: NavigationUseCase,
) : BaseFeatureOperator<LoginUiState, LoginAction, Nothing>(
    initialState = LoginUiState(),
    operatorScope = operatorScope,
) {
    override fun handleAction(action: LoginAction) {
        when (action) {
            LoginAction.GoogleLoginClicked -> loginWithGoogle()
            LoginAction.LocalAdminLoginClicked -> loginAsLocalAdmin()
            LoginAction.BackClicked -> navigationUseCase.goBack()
            LoginAction.ErrorDismissed -> updateState { copy(isError = false, errorMessage = "") }
        }
    }

    private fun loginWithGoogle() {
        val clientId = googleWebClientId()
        if (clientId.isBlank() || clientId.startsWith("REPLACE_")) {
            updateState {
                copy(
                    isError = true,
                    errorMessage = StringKeys.unconfiguredGoogleClientId,
                )
            }
            return
        }

        updateState { copy(isLoading = true, isError = false, errorMessage = "") }
        launch {
            googleLoginOperation.login(clientId).fold(
                onSuccess = { user ->
                    updateState {
                        copy(
                            isLoading = false,
                            isAuthenticated = true,
                            username = user.username ?: StringKeys.unknownUser,
                        )
                    }
                    navigationUseCase.navigateTo(RouteKMP.Diary)
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = error.message ?: StringKeys.googleLoginFailed,
                        )
                    }
                },
            )
        }
    }

    private fun loginAsLocalAdmin() {
        updateState {
            copy(
                isAuthenticated = true,
                isError = false,
                username = StringKeys.adminUsername,
                errorMessage = "",
            )
        }
        navigationUseCase.navigateTo(RouteKMP.Diary)
    }
}
