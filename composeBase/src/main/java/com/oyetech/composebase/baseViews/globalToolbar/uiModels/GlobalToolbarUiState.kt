package com.oyetech.composebase.baseViews.globalToolbar.uiModels

data class GlobalToolbarUiState(
    val title: String = "",
    val showBackButton: Boolean = false,
    val isCenterText: Boolean = false,
    val actionButtonText: String? = null,
    val onActionButtonClick: (() -> Unit)? = null,
)