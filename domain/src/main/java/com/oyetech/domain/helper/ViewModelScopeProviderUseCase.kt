package com.oyetech.domain.helper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
 * Provides a CoroutineScope from the MainActivity's ViewModel or application level.
 * Similar to ActivityProviderUseCase but for CoroutineScope management.
 *
 * Usage:
 * - In MainActivity's ViewModel: call setScope(viewModelScope) in init
 * - In repositories/use cases: inject this and use getScope() or scopeFlow
 */
class ViewModelScopeProviderUseCase {

    private var mainScope: CoroutineScope? = null

    private val _scopeFlow = MutableStateFlow<CoroutineScope?>(null)
    val scopeFlow: StateFlow<CoroutineScope?> = _scopeFlow.asStateFlow()

    // Fallback application-level scope with SupervisorJob
    private val applicationScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    private var isLoggingActive = true

    /**
     * Set the main CoroutineScope (typically viewModelScope from MainActivity's ViewModel)
     */
    fun setScope(scope: CoroutineScope) {
        if (isLoggingActive) {
            Timber.d("ViewModelScopeProvider: Setting main scope")
        }
        mainScope = scope
        _scopeFlow.value = scope
    }

    /**
     * Clear the main scope (call when ViewModel is cleared or Activity destroyed)
     */
    fun clearScope() {
        if (isLoggingActive) {
            Timber.d("ViewModelScopeProvider: Clearing main scope")
        }
        mainScope = null
        _scopeFlow.value = null
    }

    /**
     * Get the current scope. Returns mainScope if available, otherwise applicationScope.
     * applicationScope is a fallback for early initialization before MainActivity is ready.
     */
    fun getScope(): CoroutineScope {
        return mainScope ?: run {
            if (isLoggingActive) {
                Timber.w("ViewModelScopeProvider: Main scope not set, using application scope fallback")
            }
            applicationScope
        }
    }

    /**
     * Check if the main scope is currently set
     */
    fun isScopeSet(): Boolean = mainScope != null

    /**
     * Enable or disable logging
     */
    fun setLoggingActive(active: Boolean) {
        isLoggingActive = active
    }
}
