package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.snackbar.SnacbarScreenSetup
import com.oyetech.composebase.experimental.loginOperations.LoginOperationScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.generalPlayground.GeneralPlaygroundVm
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

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

    val reviewState by viewModel.getReviewCanShowState().collectAsStateWithLifecycle()
    val reviewOperationStatus by viewModel.getReviewOperationStatus().collectAsStateWithLifecycle()

    Timber.d("Review State: $reviewOperationStatus")
//
//    if (reviewState) {
//        RateUsDialog(
//            onDismiss = {
//                viewModel.dismissDialog()
//            }, onRefuse = {
//                viewModel.dismissReviewState()
//            }, onSuccess = {
//                viewModel.startReviewOperation()
//            })
//    }
//
//    var dialogState by remember { mutableStateOf(false) }
//
//    if (dialogState) {
//        RateUsDialog(
//            onDismiss = {
//                dialogState = false
//                viewModel.dismissDialog()
//            }, onRefuse = {
//                viewModel.dismissReviewState()
//            }, onSuccess = {
//                viewModel.signInWithGoogleAnonymous()
////                viewModel.updateUserName("green985")
//                //viewModel.startReviewOperation()
//
//            })
//    }



    LoginOperationScreenSetup(navigationRoute = { navController.navigate(it) })
    GeneralOperationScreen {
        content()
    }
}

@Composable
fun GeneralOperationScreen(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    BaseScaffold(snackbarHostContent = {
        SnacbarScreenSetup(snackbarHostState)
    }, contentWindowInsets = ScaffoldDefaults.contentWindowInsets) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()
        }

    }
}