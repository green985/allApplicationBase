package com.oyetech.composebase.experimental.authOperation

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent

data class AuthOperationUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    // User identity
    val userId: String = "",
    val isAnonymous: Boolean = false,
    // Complete profile form fields
    val username: String = "",
    val age: String = "",
    val gender: String = "",
    val isUsernameEmpty: Boolean = false,
    // Login state
    val isLogin: Boolean = false,

    // User profile fields (for profile completion)
    val biography: String = "",
)

sealed class AuthOperationUiEvent : BaseUIEvent() {
    object OnLoginSuccess : AuthOperationUiEvent()
    object OnProfileIncomplete : AuthOperationUiEvent()
    object OnProfileCancelled : AuthOperationUiEvent()
}

sealed class AuthOperationEvent : BaseEvent() {
    object LoginClicked : AuthOperationEvent()
    data class UsernameChanged(val username: String) : AuthOperationEvent()
    data class AgeChanged(val age: String) : AuthOperationEvent()
    data class GenderChanged(val gender: String) : AuthOperationEvent()
    object OnSubmitProfile : AuthOperationEvent()
    object OnCancelProfile : AuthOperationEvent()
    object DeleteAccountClick : AuthOperationEvent()
    object ErrorDismiss : AuthOperationEvent()
}
