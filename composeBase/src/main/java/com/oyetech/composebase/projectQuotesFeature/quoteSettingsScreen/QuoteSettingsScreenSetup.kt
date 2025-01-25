package com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationVM
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.views.SimpleSettingsInfoViewSetup
import com.oyetech.composebase.sharedScreens.quotes.views.AppInfoViewProperty
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuoteSettingsScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    generalViewModel: GeneralOperationVM = koinViewModel(),
) {
    val vm = koinViewModel<QuoteSettingsVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val toolbarState by vm.toolbarState.collectAsStateWithLifecycle()

    val startReviewOperation = {
        generalViewModel.startReviewOperation()
    }

    QuoteSettingsScreen(
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
        toolbarState = toolbarState,
        navigationRoute = navigationRoute,
        startReviewOperation = startReviewOperation
    )

}

@Composable
fun QuoteSettingsScreen(
    modifier: Modifier = Modifier,
    uiState: QuoteSettingsUiState,
    onEvent: (QuoteSettingsEvent) -> (Unit),
    toolbarState: QuoteToolbarState,
    navigationRoute: (navigationRoute: String) -> Unit,
    startReviewOperation: () -> Unit,
) {
    BaseScaffold(topBarContent = {
        QuoteToolbarSetup(
            uiState = toolbarState,
            onEvent = {
//                vm.handleToolbarAction(it)
            }
        )
    }) {
        Column(modifier = Modifier.padding(it)) {

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
