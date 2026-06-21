package com.oyetech.domain.useCases

import androidx.annotation.MainThread

class NavigationUseCase {

    private var navigateToInternal: ((Any) -> Unit)? = null
    private var goBackInternal: (() -> Unit)? = null
    private var pendingRoute: Any? = null

    fun setNavigator(navigateTo: (Any) -> Unit, goBack: () -> Unit) {
        this.navigateToInternal = navigateTo
        this.goBackInternal = goBack
        pendingRoute?.let {
            navigateTo(it)
            pendingRoute = null
        }
    }

    @MainThread
    fun navigateTo(route: Any) {
        if (navigateToInternal != null) {
            navigateToInternal?.invoke(route)
        } else {
            pendingRoute = route
        }
    }

    @MainThread
    fun goBack() {
        goBackInternal?.invoke()
    }
}
