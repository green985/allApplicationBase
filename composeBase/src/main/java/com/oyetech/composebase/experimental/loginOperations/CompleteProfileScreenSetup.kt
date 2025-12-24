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
import com.oyetech.composebase.experimental.loginOperations.LoginOperationUiEvent.OnCancelUserCreation
import com.oyetech.composebase.experimental.loginOperations.LoginOperationUiEvent.OnLoginSuccess
import com.oyetech.composebase.helpers.viewProperties.DialogHelper
import org.koin.compose.koinInject
import timber.log.Timber

@Composable
fun CompleteProfileScreenSetup() {
    val vm = koinInject<LoginOperationVM>()
    val uiState by vm.loginOperationState.collectAsStateWithLifecycle()
    val uiEventFlow by vm.uiEvent.collectAsStateWithLifecycle(null)

    when (uiEventFlow) {
        OnCancelUserCreation -> {
        }

        OnLoginSuccess -> {

            Timber.d("OnLoginSuccess navigationRoute to home")
        }

        null -> {

        }
    }

    CompleteProfileScreen(uiState = uiState, onEvent = { vm.onEvent(it) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    uiState: LoginOperationUiState,
    onEvent: (LoginOperationEvent) -> Unit,
) {
    androidx.compose.ui.window.Dialog(
        properties = DialogHelper.fullScreenDialogProperties,
        onDismissRequest = {}) {
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
                    // Handle quick screen click if needed
                }
//                    onEvent(LoginOperationEvent.OnCancel)
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Complete your profile",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Username Field
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = { onEvent(LoginOperationEvent.UsernameChanged(it)) },
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

                // Age Field
                OutlinedTextField(
                    value = uiState.age.toString(),
                    onValueChange = {
                        onEvent(LoginOperationEvent.AgeChanged(it))
                    },
                    label = { Text("Age") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                GenderSegmentedControl(
                    selectedGender = uiState.gender,
                    onGenderSelected = { onEvent.invoke(LoginOperationEvent.GenderChanged(it)) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button({ onEvent.invoke(LoginOperationEvent.OnSubmit) }) {
                    Text(text = "Set Your Profile")
                }
                Spacer(modifier = Modifier.height(64.dp))
                Button({ onEvent.invoke(LoginOperationEvent.OnCancel) }) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompleteProfileScreenPreview() {
    CompleteProfileScreen(LoginOperationUiState()) { }
}
