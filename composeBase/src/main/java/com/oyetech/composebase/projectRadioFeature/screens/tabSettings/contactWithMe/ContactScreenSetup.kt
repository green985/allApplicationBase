package com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.helpers.viewProperties.hideKeyboard
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe.ContactUIEvent.Submit
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe.ContactUIEvent.UpdateMessage
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe.ContactUIEvent.UpdateName
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent.BackButtonClick
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun ContactScreenSetup(
    viewModel: ContactViewModel = koinViewModel(),
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(uiState.isContactWasSent) {
        if (uiState.isContactWasSent) {
            Timber.d(" ContactScreen isContactWasSent")
            context.hideKeyboard()
            delay(100)
            navigationRoute.invoke("back")
        }
    }



    ContactScreenn(navigationRoute, uiState, onEvent = { viewModel.onEvent(it) })
}

@Composable
private fun ContactScreenn(
    navigationRoute: (navigationRoute: String) -> Unit,
    uiState: ContactUIState,
    onEvent: (ContactUIEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            RadioToolbarSetup(
                RadioToolbarState(title = "Contact with me", showBackButton = true),
                onEvent = {
                    if (it is BackButtonClick) {
                        navigationRoute.invoke("back")
                    }
                })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { onEvent(UpdateName(it)) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
//            OutlinedTextField(
//                value = uiState.email,
//                onValueChange = { viewModel.onEvent(ContactUIEvent.UpdateEmail(it)) },
//                label = { Text("Email (optional)") },
//                isError = !uiState.isEmailValid,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//
//
//            if (!uiState.isEmailValid) {
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "Invalid email address",
//                    color = MaterialTheme.colorScheme.error,
//                    style = MaterialTheme.typography.labelMedium
//                )
//            }

//            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.message,
                onValueChange = { onEvent(UpdateMessage(it)) },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )


            if (uiState.isDescriptionEmpty) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Message cannot be empty.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            FilledTonalButton(
                onClick = { onEvent(Submit) },
                enabled = uiState.name.isNotBlank() && uiState.message.isNotBlank() && uiState.isEmailValid
            ) {
                Text("Submit")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactScreenPreview() {
    ContactScreenn(
        navigationRoute = {}, uiState = ContactUIState(
            name = "Fernando Pugh",
            email = "perry.mckinney@example.com",
            message = "dignissim",
            isEmailValid = false,
            isDescriptionEmpty = false,
            isContactWasSent = false
        ), {}
    )
}
