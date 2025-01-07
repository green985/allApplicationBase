package com.oyetech.composebase.projectRadioFeature.navigationRoutes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationBar
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationDelegate
import com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.RadioPlayerSetup
import org.koin.compose.koinInject

@Composable
fun RadioAppNavigationWrapperWithPlayerSetup(
    navController: NavHostController,
    startDestination: String,
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioPlayerSetup() {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .background(Color.Transparent)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                    ) {
                        radioAppNavigation(navController)
                        quotesAppNavigation(navController)
                    }

                }
            }

        }

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
                navItems = RadioAppProjectRoutes.radioApplicationBottomTabNavList
            )

        }
    }

}
