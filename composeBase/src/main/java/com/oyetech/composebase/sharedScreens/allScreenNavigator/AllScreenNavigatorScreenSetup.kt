package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.base.BaseScaffold

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
//    val vm = koinViewModel<AllScreenNavigatorVM>()

//    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val isNavigate = remember { false }

    LaunchedEffect(isNavigate) {

    }

    BaseScaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(it)
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    navigationRoute.invoke(AllScreenNavigator.radioStart)
                }) {
                    Text(text = "Radio Startttt")
                }
            }

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