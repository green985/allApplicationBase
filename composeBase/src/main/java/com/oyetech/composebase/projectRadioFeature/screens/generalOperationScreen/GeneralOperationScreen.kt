package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.snackbar.SnacbarScreenSetup
import com.oyetech.composebase.experimental.loginOperations.LoginOperationScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.generalPlayground.GeneralPlaygroundVm
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-15.12.2024-
-14:03-
 **/

@Composable
fun GeneralOperationScreenSetup(
    content: @Composable () -> Unit,
    navController: NavController,
) {
    val viewModel = koinViewModel<GeneralOperationVM>()
    val generalPlaygroundVm = koinViewModel<GeneralPlaygroundVm>()

    generalPlaygroundVm.initt()


    LoginOperationScreenSetup(navigationRoute = { navController.navigate(it) })
    GeneralOperationScreen {
        content()
    }
}

@Composable
fun GeneralOperationScreen(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    BaseScaffold(
        snackbarHostContent = {
            SnacbarScreenSetup(snackbarHostState)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()
        }

    }
}