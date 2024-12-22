package com.oyetech.composebase.experimental.loginOperations

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
    val displayName: String = "",

    val uid: String = "",
    val displayNameRemote: String = "",
    val photoUrl: String = "",
    val isLogin: Boolean = uid.isNotBlank(),
    val isAnonymous: Boolean = false,

//    val creationTimestamp: Long? = null,
//    val lastSignInTimestamp: Long? = null,
)

sealed class LoginOperationEvent {
    object ErrorDismiss : LoginOperationEvent()
    object LoginClicked : LoginOperationEvent()
    object UsernameSetClicked : LoginOperationEvent()
    data class UsernameChanged(val username: String) : LoginOperationEvent()

//    object OnIdle : LoginOperationEvent()
//    object OnLoading : LoginOperationEvent()
//    object OnSuccess : LoginOperationEvent()
//    object OnError : LoginOperationEvent()
}