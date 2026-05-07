package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationUiEvent.NavigateToSelectedItemWithTest
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    vm: BottomNavigationVm = koinViewModel(),
    isClickable: Boolean = true,
    backStack: NavBackStack<NavKey>,
    navItems: List<BottomNavigationItem> = emptyList(),
) {
    // Derive the selected tab from the top of the Nav3 back stack.
    val currentDestination = backStack.lastOrNull()

    // Handle external navigation events emitted by the global TestEventNavigator bus.
    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { event ->
            when (event) {
                is NavigateToSelectedItemWithTest -> {
                    val item = navItems.getOrNull(event.index) ?: return@collectLatest
                    navigateToBottomTab(backStack, item.route)
                }
            }
        }
    }

    NavigationBar(modifier = modifier, windowInsets = WindowInsets.navigationBars) {
        navItems.forEachIndexed { _, item ->
            val title = item.titleText.ifBlank { stringResource(item.title) }
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(painterResource(item.icon), contentDescription = title) },
                label = { Text(title) },
                // Selected when the top of the back stack is the same destination class.
                selected = currentDestination?.let { it::class == item.route::class } ?: false,
                enabled = isClickable,
                onClick = { navigateToBottomTab(backStack, item.route) }
            )
        }
    }
}

/**
 * Pops the back stack to the first existing occurrence of [route]'s class, or
 * pushes a fresh instance when the tab hasn't been visited yet.
 */
fun navigateToBottomTab(backStack: NavBackStack<NavKey>, route: Any) {
    if (backStack.lastOrNull()?.let { it::class == route::class } == true) return

    val existingIndex = backStack.indexOfFirst { it::class == route::class }
    if (existingIndex >= 0) {
        // Pop entries above the existing tab root (restores its back stack).
        while (backStack.size > existingIndex + 1) {
            backStack.removeLastOrNull()
        }
    } else {
        backStack.add(route as NavKey)
    }
}
