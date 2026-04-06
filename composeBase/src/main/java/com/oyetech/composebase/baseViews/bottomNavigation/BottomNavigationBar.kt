package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationUiEvent.NavigateToSelectedItemWithTest
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    vm: BottomNavigationVm = koinViewModel(),
    isClickable: Boolean = true,
    navController: NavHostController = rememberNavController(),
    navItems: List<BottomNavigationItem> = emptyList(),
) {
    // Derive the selected tab directly from the nav back-stack so that pressing
    // the system Back button automatically reflects the correct tab.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Handle external navigation events emitted by the global TestEventNavigator bus.
    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { event ->
            when (event) {
                is NavigateToSelectedItemWithTest -> {
                    val item = navItems.getOrNull(event.index) ?: return@collectLatest
                    navigateToBottomBarRoute(navController, item.path)
                }
            }
        }
    }

    NavigationBar(modifier = modifier, windowInsets = WindowInsets.navigationBars) {
        navItems.forEachIndexed { index, item ->

            val title = item.titleText.ifBlank {
                stringResource(item.title)
            }

            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(painterResource(item.icon), contentDescription = title) },
                label = { Text(title) },
                // Selected state is always in sync with the real navigation destination.
                selected = currentRoute == item.path,
                enabled = isClickable,
                onClick = {
                    navigateToBottomBarRoute(navController, item.path)
                }
            )
        }
    }
}

fun navigateToBottomBarRoute(navController: NavHostController, route: String) {
    if (route != navController.currentDestination?.route) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            // Pop up to the start destination and save state so pressing Back
            // from any tab always returns to the start destination cleanly.
            popUpTo(findStartDestination(navController.graph)) {
                saveState = true
            }
        }
    }
}

private fun findStartDestination(graph: NavDestination): Int {
    return if (graph is NavGraph) graph.startDestinationId else graph.id
}
