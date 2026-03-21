package com.oyetech.domain.useCases

import androidx.annotation.MainThread

class NavigationUseCase {

    private var navigateToInternal: ((String) -> Unit)? = null

    fun setNavigator(navigateTo: (String) -> Unit) {
        this.navigateToInternal = navigateTo
    }

    @MainThread
    fun navigateTo(route: String) {
        navigateToInternal?.invoke(route)
    }
}
