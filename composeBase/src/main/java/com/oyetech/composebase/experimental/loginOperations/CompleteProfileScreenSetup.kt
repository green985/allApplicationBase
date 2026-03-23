package com.oyetech.composebase.experimental.loginOperations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.baseViews.helper.GenderSegmentedControl
import com.oyetech.composebase.experimental.authOperation.AuthOperationEvent
import com.oyetech.composebase.experimental.authOperation.AuthOperationUiState
import com.oyetech.composebase.experimental.authOperation.AuthOperationVM
import com.oyetech.composebase.helpers.viewProperties.DialogHelper
import org.koin.compose.koinInject
import timber.log.Timber

@Composable
fun CompleteProfileScreenSetup() {
    val vm = koinInject<AuthOperationVM>()
    val uiState by vm.authOperationState.collectAsStateWithLifecycle()
    // Navigation is handled centrally by LoginOperationVM which observes AuthOperationVM.uiEvent
    CompleteProfileScreen(uiState = uiState, onEvent = { vm.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    uiState: AuthOperationUiState,
    onEvent: (AuthOperationEvent) -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        properties = DialogHelper.fullScreenDialogProperties,
        onDismissRequest = {}
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Complete Register",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                LoginOperationQuickScreen {
                    Timber.d("LoginOperationQuickScreen clicked")
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Complete your profile",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = { onEvent(AuthOperationEvent.UsernameChanged(it)) },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.isUsernameEmpty) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Username cannot be empty.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.age,
                    onValueChange = { onEvent(AuthOperationEvent.AgeChanged(it)) },
                    label = { Text("Age") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                GenderSegmentedControl(
                    selectedGender = uiState.gender,
                    onGenderSelected = { onEvent(AuthOperationEvent.GenderChanged(it)) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button({ onEvent(AuthOperationEvent.OnSubmitProfile) }) {
                    Text(text = "Set Your Profile")
                }
                Spacer(modifier = Modifier.height(64.dp))
                Button({ onEvent(AuthOperationEvent.OnCancelProfile) }) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompleteProfileScreenPreview() {
    CompleteProfileScreen(AuthOperationUiState()) { }
}
