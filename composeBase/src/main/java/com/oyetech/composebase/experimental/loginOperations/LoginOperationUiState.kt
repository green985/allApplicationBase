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
    val isUsernameEmpty: Boolean = false,
    val isUserDeleted: Boolean = false,
    val displayName: String = "",
    val gender: String = "",
    val age: String = "",
    val isAgeInvalid: Boolean = false,

    val uid: String = "",
    val displayNameRemote: String = "",
    val photoUrl: String = "",
    val isLogin: Boolean = uid.isNotBlank(),
    val isAnonymous: Boolean = false,
    val lastSignInTimestamp: Long? = null,
)

sealed class LoginOperationUiEvent : BaseUIEvent() {
    object OnCancelUserCreation : LoginOperationUiEvent()
    object OnRegisterSuccess : LoginOperationUiEvent()
}

sealed class LoginOperationEvent : BaseEvent() {
    object ErrorDismiss : LoginOperationEvent()
    object LoginClicked : LoginOperationEvent()
    object DeleteAccountClick : LoginOperationEvent()
    data class UsernameChanged(val username: String) : LoginOperationEvent()

    data class GenderChanged(val gender: String) : LoginOperationEvent()
    data class AgeChanged(val age: String) : LoginOperationEvent()
    object OnSubmit : LoginOperationEvent()
    object OnCancel : LoginOperationEvent()
}