package com.oyetech.kmpmodels.ui.event

sealed interface HomeEvent {
    data object LoginClicked : HomeEvent
    data object ErrorDismissed : HomeEvent
}
