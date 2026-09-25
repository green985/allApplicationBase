package com.oyetech.kmpdomain

import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainTest {
    @Test
    fun pendingRouteIsDeliveredWhenNavigatorIsAttached() {
        val navigation = NavigationUseCase()
        val route = "pending-route"
        var delivered: Any? = null

        navigation.navigateTo(route)
        navigation.setNavigator(
            navigateTo = { delivered = it },
            goBack = {},
        )

        assertEquals(route, delivered)
    }

    @Test
    fun snackbarStateIsUpdated() {
        val delegate = SnackbarDelegate()

        delegate.triggerSnackbarState("Done", "Close") {}

        assertEquals("Done", delegate.snackbarUiState.value.message)
        assertEquals("Close", delegate.snackbarUiState.value.actionLabel)
    }
}
