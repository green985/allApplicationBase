package com.oyetech.composebase.projectQuotesFeature.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavHostController
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationBar
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationDelegate
import org.koin.compose.koinInject

/**
Created by Erdi Özbek
-25.01.2025-
-16:43-
 **/

@Composable
fun QuoteBottomNavigationView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    val bottomNavigationDelegate = koinInject<BottomNavigationDelegate>()

    val bottomNavigationVisibility by bottomNavigationDelegate.bottomNavigationVisibilityState.collectAsState()
    val alpha by animateFloatAsState(
        targetValue = if (bottomNavigationVisibility) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
    )

    {
        BottomNavigationBar(
            isClickable = bottomNavigationVisibility,
            navController = navController,
            navItems = QuoteAppProjectRoutes.quoteApplicationBottomTabNavList
        )

    }
}