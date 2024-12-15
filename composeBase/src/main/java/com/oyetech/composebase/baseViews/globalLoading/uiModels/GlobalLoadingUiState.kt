package com.oyetech.composebase.baseViews.globalLoading.uiModels

sealed class GlobalLoadingUiState {
    data object Idle : GlobalLoadingUiState()  // Hiçbir şey gösterilmiyor
    data object Loading : GlobalLoadingUiState()  // Yükleniyor ekranı gösteriliyor
    data class Error(val message: String, val errorAction: (() -> Unit)? = null) :
        GlobalLoadingUiState()  // Hata ekranı gösteriliyor
}