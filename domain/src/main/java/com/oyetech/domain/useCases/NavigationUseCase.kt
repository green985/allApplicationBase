package com.oyetech.domain.useCases

import androidx.annotation.MainThread

class NavigationUseCase {

    private var navigateToInternal: ((Any) -> Unit)? = null
    private var goBackInternal: (() -> Unit)? = null

    fun setNavigator(navigateTo: (Any) -> Unit, goBack: () -> Unit) {
        this.navigateToInternal = navigateTo
        this.goBackInternal = goBack
    }

    @MainThread
    fun navigateTo(route: Any) {
        navigateToInternal?.invoke(route)
    }

    @MainThread
    fun goBack() {
        goBackInternal?.invoke()
    }
}
