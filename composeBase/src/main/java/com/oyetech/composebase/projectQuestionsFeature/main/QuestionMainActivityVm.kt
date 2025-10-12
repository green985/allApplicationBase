package com.oyetech.composebase.projectQuestionsFeature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oyetech.domain.helper.ViewModelScopeProviderUseCase
import timber.log.Timber

/**
 * ViewModel for QuestionMainActivity.
 * Sets the viewModelScope in ViewModelScopeProviderUseCase for use throughout the app.
 *
 * This allows repositories and other components to use a scope tied to the
 * MainActivity's lifecycle instead of GlobalScope or ApplicationScope.
 */
class QuestionMainActivityVm(
    private val viewModelScopeProviderUseCase: ViewModelScopeProviderUseCase,
) : ViewModel() {

    init {
        // Set the viewModelScope to be used app-wide
        viewModelScopeProviderUseCase.setScope(viewModelScope)
        Timber.d("QuestionMainActivityVm initialized, viewModelScope set in provider")
    }

    override fun onCleared() {
        super.onCleared()
        // Clear the scope when ViewModel is destroyed
        viewModelScopeProviderUseCase.clearScope()
        Timber.d("QuestionMainActivityVm cleared, scope cleared in provider")
    }
}
