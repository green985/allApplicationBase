package com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.quotes.views.AppInfoViewProperty
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.GeneralOperationVM
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.views.SimpleSettingsInfoViewSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.DeleteAccountInfoDialog
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.compose.koinInject

@Suppress("FunctionName")
@Composable
fun QuoteSettingsScreenSetup(
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val generalViewModel: GeneralOperationVM = koinInject<GeneralOperationVM>()
    val vm = koinInject<QuoteSettingsVm>()

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

    if (uiState.isDeleteDialogShown) {
        DeleteAccountInfoDialog(
            onDismiss = { vm.onEvent(QuoteSettingsEvent.DismissDialog) },
            onConfirm = { vm.onEvent(QuoteSettingsEvent.DeleteAccountConfirm) }
        )
    }

}

@Suppress("FunctionName", "LongParameterList", "LongMethod")
@Composable
fun QuoteSettingsScreen(
//    modifier: Modifier = Modifier,
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
                onClick = { navigationRoute.invoke(QuoteAppProjectRoutes.ContactScreen.route) },
                text = LanguageKey.contactWithUs
            )
            if (GeneralSettings.isRatingEnable()) {
                HorizontalDivider(
                    modifier = Modifier.height(1.dp)
                )
                SimpleSettingsInfoViewSetup(
                    onClick = { startReviewOperation.invoke() },
                    text = LanguageKey.rateUs
                )
            }

            if (GeneralSettings.isLoginOperationEnable()) {
                if (uiState.isUserLoggedIn) {
                    HorizontalDivider(
                        modifier = Modifier.height(1.dp)
                    )
                    SimpleSettingsInfoViewSetup(
                        onClick = {
                            navigationRoute.invoke(QuoteAppProjectRoutes.QuoteAdviceScreen.route)
                        },
                        text = LanguageKey.adviceQuote
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.height(1.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isUserLoggedIn) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = LanguageKey.usernameInfoText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        onEvent.invoke(QuoteSettingsEvent.DeleteAccountClick)
                    }) {
                        Text(text = LanguageKey.deleteAccountButtonText)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AppInfoViewProperty()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_QuoteSettingsScreen() {
    QuoteSettingsScreen(
        uiState = QuoteSettingsUiState(
            isUserLoggedIn = true,
            username = "john_doe"
        ),
        onEvent = {},
        toolbarState = QuoteToolbarState(
            title = "Settings"
        ),
        navigationRoute = {},
        startReviewOperation = {}
    )
}


