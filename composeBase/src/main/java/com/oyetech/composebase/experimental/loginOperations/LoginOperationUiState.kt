package com.oyetech.composebase.experimental.loginOperations

/**
Created by Erdi Özbek
-22.12.2024-
-02:25-
 **/

data class LoginOperationUiState(
    val isIdle: Boolean = true,
)

sealed class LoginOperationEvent {
//    object OnIdle : LoginOperationEvent()
//    object OnLoading : LoginOperationEvent()
//    object OnSuccess : LoginOperationEvent()
//    object OnError : LoginOperationEvent()
}