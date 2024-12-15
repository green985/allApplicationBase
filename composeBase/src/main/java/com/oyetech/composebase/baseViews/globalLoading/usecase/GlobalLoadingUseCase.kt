package com.oyetech.composebase.baseViews.globalLoading.usecase

import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GlobalLoadingUseCase {
    private val _uiState = MutableStateFlow<GlobalLoadingUiState>(GlobalLoadingUiState.Idle)
    val uiState: StateFlow<GlobalLoadingUiState> = _uiState

    init {
    }

    fun showLoading() {
        _uiState.value = GlobalLoadingUiState.Loading
    }

    fun hideLoading() {
        _uiState.value = GlobalLoadingUiState.Idle
    }

    fun showError(errorMessage: String) {
        _uiState.value = GlobalLoadingUiState.Error(errorMessage)
    }

    fun retry() {
        _uiState.value = GlobalLoadingUiState.Loading // Tekrar deneme durumu
    }
}