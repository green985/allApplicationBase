package com.oyetech.domain.useCases

class NavigationUseCase {

    private var navigateToInternal: ((String) -> Unit)? = null

    fun setNavigator(navigateTo: (String) -> Unit) {
        this.navigateToInternal = navigateTo
    }

    fun navigate(route: String) {
        navigateToInternal?.invoke(route)
    }
}