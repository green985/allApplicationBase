package com.oyetech.composebase.projectQuestionsFeature.navigation

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
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationVm
import com.oyetech.composebase.helpers.viewProperties.keyboardAsState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun QuestionAppBottomNavigationView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val bottomNavigationDelegate = koinInject<BottomNavigationDelegate>()
    val bottomNavigationVisibility by bottomNavigationDelegate.bottomNavigationVisibilityState.collectAsState()
    val alpha by animateFloatAsState(
        targetValue = if (bottomNavigationVisibility) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    val isKeyboardOpen by keyboardAsState()
    val bottomNavigationVm = koinViewModel<BottomNavigationVm>()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
    ) {
        if (!isKeyboardOpen)
            BottomNavigationBar(
                isClickable = bottomNavigationVisibility,
                vm = bottomNavigationVm,
                navController = navController,
                navItems = QuestionAppProjectBottomNavigationDestinations.questionApplicationBottomTabNavList
            )
    }
}
