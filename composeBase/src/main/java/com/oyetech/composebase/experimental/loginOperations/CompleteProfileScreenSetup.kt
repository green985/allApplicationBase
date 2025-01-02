package com.oyetech.composebase.experimental.loginOperations

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.helpers.viewProperties.DialogHelper
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-22.12.2024-
-19:34-
 **/

@Composable
fun CompleteProfileScreenSetup(navigationRoute: (navigationRoute: String) -> Unit = {}) {
    val vm = koinViewModel<LoginOperationVM>()

    val uiState by vm.loginOperationState.collectAsStateWithLifecycle()



    CompleteProfileScreen(uiState = uiState, onEvent = { vm.handleEvent(it) }, navigationRoute)

    if (uiState.displayNameRemote.isNotBlank()) {
        LaunchedEffect(uiState.displayNameRemote) {
            // make snacbar success message
            navigationRoute.invoke("back")
        }
    }


    BackHandler {
        // your action
        Timber.d("BackHandler")
    }
}

@Composable
fun CompleteProfileScreen(
    uiState: LoginOperationUiState,
    onEvent: (LoginOperationEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    androidx.compose.ui.window.Dialog(
        properties = DialogHelper.fullScreenDialogProperties,
        onDismissRequest = {}) {
        Scaffold(
            topBar = {
                RadioToolbarSetup(
                    RadioToolbarState(title = "Complete Register")
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
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Complete your profile",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(
                    value = uiState.displayName,
                    onValueChange = { onEvent(LoginOperationEvent.UsernameChanged(it)) },
                    label = { Text("Name") },
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
                Button({ onEvent.invoke(LoginOperationEvent.UsernameSetClicked) }) {
                    Text(text = "Set Your UserName")
                }
            }
        }
    }
}