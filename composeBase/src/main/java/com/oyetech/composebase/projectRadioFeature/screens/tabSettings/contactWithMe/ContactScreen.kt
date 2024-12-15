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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun ContactScreen(
    viewModel: ContactViewModel = koinViewModel(),
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isContactWasSent) {
        if (uiState.isContactWasSent) {
            Timber.d(" ContactScreen isContactWasSent")
            navigationRoute.invoke("back")
        }
    }



    Scaffold(
        topBar = {
            RadioToolbarSetup(
                RadioToolbarState(title = "Contact with me", showBackButton = true),
                onEvent = {
                    if (it is RadioToolbarEvent.BackButtonClick) {
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
                onValueChange = { viewModel.onEvent(ContactUIEvent.UpdateName(it)) },
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
                onValueChange = { viewModel.onEvent(ContactUIEvent.UpdateMessage(it)) },
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
                onClick = { viewModel.onEvent(ContactUIEvent.Submit) },
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
    ContactScreen()
}
