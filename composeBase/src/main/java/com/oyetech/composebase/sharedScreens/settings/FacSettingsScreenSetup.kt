package com.oyetech.composebase.sharedScreens.settings

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
import com.oyetech.composebase.sharedViews.settings.SimpleSettingsInfoViewSetup
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-25.03.2025-
-21:51-
 **/

@Suppress("FunctionName")
@Composable
fun FacSettingsScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<FacSettingsVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val toolbarTitle by vm.toolbarTitle.collectAsStateWithLifecycle()

    FacSettingsScreen(
        uiState = uiState,
        toolbarTitle = toolbarTitle,
        onEvent = { vm.onEvent(it) },
    )
//
    if (uiState.isDeleteAccountShown) {
        com.oyetech.composebase.sharedViews.dialogs.DeleteAccountInfoDialog(
            onDismiss = { vm.onEvent(FacSettingsUiEvent.DeleteAccountDismissed) },
            onConfirm = { vm.onEvent(FacSettingsUiEvent.DeleteAccountConfirmed) }
        )
    }
//    if (false && uiState.isInfoDialogShown) {
//        InfoDialogOperation(
//            onDismiss = { vm.onEvent(FacSettingsUiEvent.InfoDialogDismissed) },
//            titleText = "Deneme Title",
//            descriptionText = "Deneme Description"
//        )
//    }
}

@Suppress("FunctionName", "LongParameterList")
@Composable
fun FacSettingsScreen(
    modifier: Modifier = Modifier,
    uiState: FacSettingsUiState,
    onEvent: (FacSettingsUiEvent) -> (Unit),
    toolbarTitle: String,
    startReviewOperation: () -> Unit = {},
) {
    BaseScaffold(topBar = {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            text = toolbarTitle,
            style = MaterialTheme.typography.titleLarge
        )
    }) {
        Column(modifier = Modifier.padding(it)) {
            SimpleSettingsInfoViewSetup(
                onClick = { onEvent.invoke(FacSettingsUiEvent.ContactClicked) },
                text = uiState.contactWithMeText
            )
            SimpleSettingsInfoViewSetup(
                onClick = { onEvent.invoke(FacSettingsUiEvent.InfoClicked) },
                text = uiState.infoText
            )
            SimpleSettingsInfoViewSetup(
                onClick = { onEvent.invoke(FacSettingsUiEvent.PrivacyPolicyClicked) },
                text = uiState.privacyPolicyText
            )
            SimpleSettingsInfoViewSetup(
                onClick = { onEvent.invoke(FacSettingsUiEvent.TermsAndConditionsClicked) },
                text = uiState.termsAndConditionText
            )
            if (GeneralSettings.isRatingEnable()) {
                HorizontalDivider(
                    modifier = Modifier.height(1.dp)
                )
                SimpleSettingsInfoViewSetup(
                    onClick = { startReviewOperation.invoke() },
                    text = uiState.rateUs
                )
            }

            if (uiState.isUserLoggedIn) {
                Spacer(modifier = Modifier.height(16.dp))

                SimpleSettingsInfoViewSetup(
                    onClick = { onEvent.invoke(FacSettingsUiEvent.NavigateToProfile) },
                    text = "My Profile"
                )

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
                        onEvent.invoke(FacSettingsUiEvent.DeleteAccountClicked)
                    }) {
                        Text(text = uiState.deleteAccountInfoText)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isDebug) {
                HorizontalDivider(modifier = Modifier.height(1.dp))
                SimpleSettingsInfoViewSetup(
                    onClick = { onEvent.invoke(FacSettingsUiEvent.AdminApproveQuestionsClicked) },
                    text = "Admin: Approve Questions"
                )
            }

            Text(text = LanguageKey.appName)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

@Preview(showSystemUi = false, showBackground = true)
@Composable
private fun FacSettingsScreenPreview() {
//    FacSettingsScreen(
//        uiState = FacSettingsUiState(),
//        onEvent = {},
//    )
}