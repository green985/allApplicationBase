package com.oyetech.composebase.baseViews.globalToolbar.uiModels

sealed class GlobalToolbarUiEvent {
    data object OnBackButtonClick : GlobalToolbarUiEvent()
    data object OnActionButtonClick : GlobalToolbarUiEvent()
}
