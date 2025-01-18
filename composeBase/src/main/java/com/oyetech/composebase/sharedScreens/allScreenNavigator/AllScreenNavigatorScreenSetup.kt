package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-18.01.2025-
-11:09-
 **/

@Composable
fun AllScreenNavigatorScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<AllScreenNavigatorVM>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    BaseScaffold {
        Column(modifier = Modifier.padding()) {

        }
    }

}

@Composable
fun AllScreenNavigatorVMScreen(
    modifier: Modifier = Modifier,
    uiState: AllScreenNavigatorUiState,
    onEvent: (AllScreenNavigatorEvent) -> (Unit),
) {
    BaseScaffold {
        Column(modifier = Modifier.padding()) {

        }
    }

}