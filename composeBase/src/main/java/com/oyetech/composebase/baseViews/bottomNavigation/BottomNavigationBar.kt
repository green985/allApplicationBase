package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
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

    val selectedItem by vm.selectedItem.collectAsState()



    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { event ->
            when (event) {
                is NavigateToSelectedItemWithTest -> {
                    performBottomNavigation(
                        navController = navController,
                        item = navItems[event.index]
                    )
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
                selected = selectedItem == index,
                enabled = isClickable,
                onClick = {
                    vm.onEvent(
                        BottomNavigationEvent.NavigateToSelectedItem(index)
                    )
                    performBottomNavigation(navController, item)
                }
            )
        }
    }

}

private fun performBottomNavigation(
    navController: NavHostController,
    item: BottomNavigationItem,
) {
    navigateToBottomBarRoute(navController, item.path)
}

fun navigateToBottomBarRoute(navController: NavHostController, route: String) {
    if (route != navController.currentDestination?.route) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            // Pop up backstack to the first destination and save state. This makes going back
            // to the start destination when pressing back in any other bottom tab.
            popUpTo(findStartDestination(navController.graph)) {
                saveState = true
            }
        }
    }
}

private tailrec fun findStartDestination(graph: NavDestination): Int {
    return if (graph is NavGraph) graph.startDestinationId else graph.id
}