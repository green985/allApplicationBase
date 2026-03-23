package com.oyetech.composebase.experimental.loginOperations

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent

/**
Created by Erdi Özbek
-22.12.2024-
-02:25-
 **/

data class LoginOperationUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isUserDeleted: Boolean = false,
    val username: String = "",
    // true when the user has a non-empty display name (set after successful login)
    val isLogin: Boolean = username.isNotBlank(),
    val isRegistrationCompleteNeeded: Boolean = false,
)

sealed class LoginOperationUiEvent : BaseUIEvent() {
    object OnCancelUserCreation : LoginOperationUiEvent()
    object OnLoginSuccess : LoginOperationUiEvent()
}

sealed class LoginOperationEvent : BaseEvent() {
    object ErrorDismiss : LoginOperationEvent()
    object LoginClicked : LoginOperationEvent()
    object DeleteAccountClick : LoginOperationEvent()
}
