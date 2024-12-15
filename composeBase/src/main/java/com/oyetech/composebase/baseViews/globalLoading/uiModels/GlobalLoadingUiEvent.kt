package com.oyetech.composebase.baseViews.globalLoading.uiModels

sealed class GlobalLoadingUiEvent {
    data object Retry : GlobalLoadingUiEvent()  // Tekrar deneme butonuna basıldı
    data object Dismiss : GlobalLoadingUiEvent()  // Tekrar deneme butonuna basıldı
}