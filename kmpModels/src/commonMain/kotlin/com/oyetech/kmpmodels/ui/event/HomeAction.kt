package com.oyetech.kmpmodels.ui.event

sealed interface HomeAction {
    data object LoginClicked : HomeAction
    data object OperatorExampleClicked : HomeAction
    data object ErrorDismissed : HomeAction
}
