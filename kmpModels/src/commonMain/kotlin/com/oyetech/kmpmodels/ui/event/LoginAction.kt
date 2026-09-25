package com.oyetech.kmpmodels.ui.event

sealed interface LoginAction {
    data object GoogleLoginClicked : LoginAction
    data object LocalAdminLoginClicked : LoginAction
    data object BackClicked : LoginAction
    data object ErrorDismissed : LoginAction
}
