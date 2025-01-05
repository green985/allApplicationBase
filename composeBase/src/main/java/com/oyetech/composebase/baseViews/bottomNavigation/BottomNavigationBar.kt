package com.oyetech.composebase.baseViews.bottomNavigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    isClickable: Boolean = true,
    navController: NavHostController = rememberNavController(),
    navItems: List<BottomNavigationItem> = RadioAppProjectRoutes.radioApplicationBottomTabNavList,
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }


    NavigationBar(modifier = modifier, windowInsets = WindowInsets.navigationBars) {
        navItems.forEachIndexed { index, item ->
            val title = stringResource(item.title)

            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(painterResource(item.icon), contentDescription = title) },
                label = { Text(title) },
                selected = selectedItem == index,
                enabled = isClickable,
                onClick = {
                    selectedItem = index
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