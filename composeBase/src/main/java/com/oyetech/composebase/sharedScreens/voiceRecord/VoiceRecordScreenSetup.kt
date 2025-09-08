/**
Created by Erdi Özbek
-03.09.2025-
-Digital Planner-
 **/

package com.oyetech.composebase.sharedScreens.voiceRecord

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.helpers.viewProperties.OnResumeEffect
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionName")
@Composable
fun VoiceRecordScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<VoiceRecordVm>()
    OnResumeEffect { vm.onEvent(VoiceRecordEvent.OnScreenOpen) }
    val uiState by vm.uiState.collectAsStateWithLifecycle()
//
//    val snackbarHostState = remember { SnackbarHostState() }
//    LaunchedEffect(uiState.snackbarMessage) {
//        val message = uiState.snackbarMessage
//        if (!message.isNullOrBlank()) {
//            snackbarHostState.showSnackbar(message)
//            vm.onEvent(VoiceRecordEvent.OnSnackbarShown)
//        }
//    }

    BaseScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = LanguageKey.voiceRecorderTitle,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        VoiceRecordScreen(
            uiState = uiState,
            onEvent = { vm.onEvent(it) },
            contentPadding = innerPadding
        )
    }
}

@Suppress("FunctionName")
@Composable
fun VoiceRecordScreen(
    uiState: VoiceRecordUiState,
    onEvent: (VoiceRecordEvent) -> Unit,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (uiState.isRecording) LanguageKey.recordingStatusOn else LanguageKey.recordingStatusOff,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = uiState.elapsedFormatted,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                if (uiState.isRecording) onEvent(VoiceRecordEvent.StopRecording)
                else onEvent(VoiceRecordEvent.StartRecording)
            }
        ) {
            Text(
                text = if (uiState.isRecording) LanguageKey.stopRecording else LanguageKey.startRecording,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

data class VoiceRecordUiState(
    val isRecording: Boolean = false,
    val elapsedFormatted: String = "00:00",
    val snackbarMessage: String? = null,
)

sealed class VoiceRecordEvent {
    data object OnScreenOpen : VoiceRecordEvent()
    data object StartRecording : VoiceRecordEvent()
    data object StopRecording : VoiceRecordEvent()
    data object OnSnackbarShown : VoiceRecordEvent()
}

@Preview(showBackground = true)
@Composable
private fun VoiceRecordScreenPreview_Idle() {
    VoiceRecordScreen(
        uiState = VoiceRecordUiState(
            isRecording = false,
            elapsedFormatted = "00:00",
            snackbarMessage = null
        ),
        onEvent = {},
        contentPadding = PaddingValues(0.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun VoiceRecordScreenPreview_Recording() {
    VoiceRecordScreen(
        uiState = VoiceRecordUiState(
            isRecording = true,
            elapsedFormatted = "00:42",
            snackbarMessage = null
        ),
        onEvent = {},
        contentPadding = PaddingValues(0.dp)
    )
}