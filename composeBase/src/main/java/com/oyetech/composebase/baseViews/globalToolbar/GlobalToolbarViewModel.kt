package com.oyetech.composebase.baseViews.globalToolbar

import androidx.lifecycle.ViewModel
import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiEvent
import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiState
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class GlobalToolbarViewModel(
    private val globalToolbarUseCase: GlobalToolbarUseCase,
) : ViewModel() {

    init {
        Timber.d(" GlobalToolbarViewModel created")
    }

    val uiState: StateFlow<GlobalToolbarUiState> = globalToolbarUseCase.uiState

    fun handleEvent(event: GlobalToolbarUiEvent) {
        when (event) {
            is GlobalToolbarUiEvent.OnBackButtonClick -> {
                // Geri butonu tıklaması için gerekli işlemler
                println("Back button clicked")
            }

            is GlobalToolbarUiEvent.OnActionButtonClick -> {
                uiState.value.onActionButtonClick?.invoke()
            }
        }
    }

    // Toolbar ile ilgili işlemler için gerekli fonksiyonlar
    fun updateTitle(newTitle: String) {
        globalToolbarUseCase.setTitle(newTitle)
    }

    fun showBackButton(show: Boolean) {
        globalToolbarUseCase.showBackButton(show)
    }

    fun setActionButton(buttonText: String, onClick: () -> Unit) {
        globalToolbarUseCase.setActionButton(buttonText, onClick)
    }

    fun clearActionButton() {
        println("clearActionButton")

        globalToolbarUseCase.clearActionButton()
    }
}
