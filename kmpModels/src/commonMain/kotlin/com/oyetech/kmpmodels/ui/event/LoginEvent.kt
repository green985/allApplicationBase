package com.oyetech.kmpmodels.ui.event

sealed interface LoginEvent {
    data object GoogleLoginClicked : LoginEvent
    data object LocalAdminLoginClicked : LoginEvent
    data object BackClicked : LoginEvent
    data object ErrorDismissed : LoginEvent
}
