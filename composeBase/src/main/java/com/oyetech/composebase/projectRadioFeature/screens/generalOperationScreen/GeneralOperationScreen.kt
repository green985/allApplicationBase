package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.generalPlayground.GeneralPlaygroundVm
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.RateUsDialog
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-15.12.2024-
-14:03-
 **/

@Composable
fun GeneralOperationScreenSetup(content: @Composable () -> Unit) {
    val viewModel = koinViewModel<GeneralOperationVM>()
    val generalPlaygroundVm = koinViewModel<GeneralPlaygroundVm>()

    generalPlaygroundVm.initt()

    val reviewState by viewModel.getReviewCanShowState().collectAsStateWithLifecycle()
    val reviewOperationStatus by viewModel.getReviewOperationStatus().collectAsStateWithLifecycle()

    Timber.d("Review State: $reviewOperationStatus")

    if (reviewState) {
        RateUsDialog(onDismiss = {
            viewModel.dismissReviewState()
        }) {
            viewModel.startReviewOperation()
        }
    }




    GeneralOperationScreen {
        content()
    }
}

@Composable
fun GeneralOperationScreen(content: @Composable () -> Unit) {
    BaseScaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            content()
        }
    }

}