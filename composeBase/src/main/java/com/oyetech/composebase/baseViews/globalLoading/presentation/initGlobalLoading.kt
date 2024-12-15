package com.oyetech.composebase.baseViews.globalLoading.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.oyetech.composebase.baseViews.globalLoading.GlobalLoadingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun initGlobalLoading() {
    val vm = koinViewModel<GlobalLoadingViewModel>()

    val uiState by vm.uiState.collectAsState()

    GlobalLoadingScreen(uiState = uiState) {
        vm.onEvent(it)
    }
}