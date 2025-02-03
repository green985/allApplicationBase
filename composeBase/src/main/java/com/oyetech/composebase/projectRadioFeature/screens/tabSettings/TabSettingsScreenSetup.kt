package com.oyetech.composebase.projectRadioFeature.screens.tabSettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuotesFeature.quotes.views.AppInfoViewProperty
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationVM
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.views.SimpleSettingsInfoViewSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-13.12.2024-
-12:27-
 **/

@Composable
fun TabSettingsScreenSetup(
    navigationRoute: (navigationRoute: String) -> Unit = {},
    viewModel: TabSettingsVM = koinViewModel(),
    generalViewModel: GeneralOperationVM = koinViewModel(),
) {
    val toolbarUiState by viewModel.toolbarUiState.collectAsStateWithLifecycle()

    val startReviewOperation = {
        generalViewModel.startReviewOperation()
    }

    TabSettingsScreen(navigationRoute, startReviewOperation, toolbarUiState)

}

@Composable
fun TabSettingsScreen(
    navigationRoute: (navigationRoute: String) -> Unit,
    startReviewOperation: () -> Unit,
    toolbarUiState: RadioToolbarState,
) {

    BaseScaffold(
        topBarContent = {
            RadioToolbarSetup(toolbarUiState)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
        {
            Spacer(modifier = Modifier.height(32.dp))

            SimpleSettingsInfoViewSetup(
                onClick = { navigationRoute.invoke(RadioAppProjectRoutes.ContactScreen.route) },
                text = "Contact with us"
            )
            HorizontalDivider(
                modifier = Modifier.height(1.dp)
            )
            SimpleSettingsInfoViewSetup(
                onClick = { startReviewOperation.invoke() },
                text = "Rate us"
            )

            HorizontalDivider(
                modifier = Modifier.height(1.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            AppInfoViewProperty()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun PreviewTabSettingsScreenSetup() {
    TabSettingsScreen(
        navigationRoute = {},
        startReviewOperation = {},
        toolbarUiState = RadioToolbarState(
            title = "Settings",
            showBackButton = true,
        )

    )
}