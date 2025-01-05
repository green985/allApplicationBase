package com.oyetech.composebase.projectRadioFeature.screens.radioPlayer

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationDelegate
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.vm.RadioPlayerVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import timber.log.Timber

/**
Created by Erdi Özbek
-20.11.2024-
-22:54-
 **/
@SuppressLint("FunctionNaming")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RadioPlayerSetup(
    navigationRoute: (navigationRoute: String) -> Unit = {},
    radioPlayerVM: RadioPlayerVM = koinViewModel(),
    content: @Composable () -> Unit = {},
) {
    val radioUiState by radioPlayerVM.radioUIState.collectAsStateWithLifecycle()

    val isFirstRadioInit = radioUiState.radioName.isNotBlank()

    val sheetPeekHeight = if (isFirstRadioInit) {
        RadioDimensions.radioSmallPlayerSizeHeight
    } else {
        0.dp
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    val isVisible = (scaffoldState.bottomSheetState.currentValue == Expanded).not()
//    val alphaValue = if (scaffoldState.bottomSheetState.currentValue == Expanded) {
//        0f
//    } else {
//        1f
//    }
//    val alpha by animateFloatAsState(
//        targetValue = 1f, // 1f is fully visible, 0f is fully transparent
//        animationSpec = tween(durationMillis = 1000) // 1 second duration
//    )
//    val drawerOffsetX = animateDpAsState(targetValue = if (isVisible) 0.dp else (-300).dp)

    val isFullUiVisible = scaffoldState.bottomSheetState.currentValue == Expanded

    val bottomNavigationDelegate = koinInject<BottomNavigationDelegate>()

    LaunchedEffect(isFullUiVisible) {
        Timber.d("FullRadioPlayer2: LaunchedEffect")
        bottomNavigationDelegate.triggerBottomNavigationVisibilityState(!isFullUiVisible)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetDragHandle = {},
        sheetPeekHeight = sheetPeekHeight,
        sheetShape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isFirstRadioInit) {

                    val alpha by animateFloatAsState(
                        targetValue = if (isVisible) 1f else 0f,
                        animationSpec = tween(durationMillis = 300)
                    )
                    SmallRadioPlayer(
                        modifier = Modifier.alpha(alpha),
                        radioPlayerUIState = radioUiState,
                        radioPlayerEvent = {
                            radioPlayerVM.handleRadioEvent(it, null)
                        }
                    )
                    FullRadioPlayer2(
                        uiState = radioUiState,
                        navigationRoute = navigationRoute,
                        radioPlayerEvent = { radioPlayerVM.handleRadioEvent(it, null) }
                    )
                }
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        })
}