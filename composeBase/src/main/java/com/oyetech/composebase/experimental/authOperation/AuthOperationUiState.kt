package com.oyetech.composebase.experimental.authOperation

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent
import com.oyetech.models.firebaseModels.userModel.UserDataProperty

data class AuthOperationUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val userDataProperty: UserDataProperty? = null,
)

sealed class AuthOperationUiEvent : BaseUIEvent() {
    object OnLoginSuccess : AuthOperationUiEvent()
}

sealed class AuthOperationEvent : BaseEvent() {
    object LoginClicked : AuthOperationEvent()
}

