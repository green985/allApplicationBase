package com.oyetech.composebase.baseViews.globalToolbar

import androidx.lifecycle.ViewModel
import com.oyetech.composebase.baseViews.globalToolbar.delegate.GlobalToolbarDelegate
import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiEvent

/**
Created by Erdi Özbek
-13.10.2024-
-19:44-
 **/

class GlobalToolbarViewModelWithDelegate(private val globalToolbarDelegate: GlobalToolbarDelegate) :
    ViewModel(), GlobalToolbarDelegate by globalToolbarDelegate {

    val uiState = globalToolbarUiState

    init {

    }

    fun handleEvent(event: GlobalToolbarUiEvent) {
        when (event) {
            is GlobalToolbarUiEvent.OnBackButtonClick -> {
                // Geri butonu tıklaması için gerekli işlemler
                println("Back button clicked")
            }

            is GlobalToolbarUiEvent.OnActionButtonClick -> {
                onActionButtonClick()
            }
        }
    }

    private fun exampleFunc() {
        println("Action button clicked")
    }
}