package com.oyetech.kmpdomain.usecase.navigation

class NavigationUseCase {
    private var navigateToInternal: ((Any) -> Unit)? = null
    private var goBackInternal: (() -> Unit)? = null
    private var pendingRoute: Any? = null

    fun setNavigator(navigateTo: (Any) -> Unit, goBack: () -> Unit) {
        navigateToInternal = navigateTo
        goBackInternal = goBack
        pendingRoute?.let { route ->
            navigateTo(route)
            pendingRoute = null
        }
    }

    fun clearNavigator() {
        navigateToInternal = null
        goBackInternal = null
    }

    fun navigateTo(route: Any) {
        val navigator = navigateToInternal
        if (navigator != null) {
            navigator(route)
        } else {
            pendingRoute = route
        }
    }

    fun goBack() {
        goBackInternal?.invoke()
    }
}
