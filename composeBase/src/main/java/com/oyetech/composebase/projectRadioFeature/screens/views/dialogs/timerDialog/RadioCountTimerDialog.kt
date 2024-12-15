package com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.oyetech.composebase.R
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-9.12.2024-
-22:21-
 **/

@Composable
fun RadioCountTimerDialogSetup(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: RadioCountTimerViewModel = koinViewModel<RadioCountTimerViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isDialogVisible) {
        RadioCountTimerDialog(
            modifier = modifier,
            onDismiss = {
                navController.navigateUp()
                viewModel.onEvent(RadioCountTimerUIEvent.DismissDialog)
            },
            sliderPosition = uiState.sliderPosition,
            sliderTimeText = uiState.sliderTimeText,
            onBarValueChange = {
                viewModel.onEvent(RadioCountTimerUIEvent.UpdateSliderPosition(it))
            },
            onTimerApply = { viewModel.onEvent(RadioCountTimerUIEvent.TimerApply) },
            onClear = { viewModel.onEvent(RadioCountTimerUIEvent.TimerClear) },
            onUserInteract = { viewModel.onEvent(RadioCountTimerUIEvent.UserInteract) },
        )
    }

}

@Suppress("FunctionName")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioCountTimerDialog(
    modifier: Modifier,
    onDismiss: () -> Unit,
    sliderPosition: Float,
    sliderTimeText: String = "",
    onBarValueChange: (Float) -> Unit,
    onTimerApply: () -> Unit,
    onClear: () -> Unit,
    onUserInteract: () -> Unit = {},
) {
    BasicAlertDialog(onDismiss,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),

        properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true
        ),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.sleep_timer_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = sliderPosition,
                    onValueChange = {
                        onBarValueChange(it)
                        onUserInteract.invoke()
                    },
                    valueRange = 0f..100f,
                    steps = 19 // 5, 10, 15, ..., 100
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = sliderTimeText,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {

                    TextButton(onClick = {
                        onDismiss.invoke()
                        onClear.invoke()
                    }) {
                        Text(text = stringResource(R.string.sleep_timer_clear))
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Button(onClick = {
                        onTimerApply.invoke()
                        onDismiss.invoke()
                    }) {
                        Text(text = stringResource(R.string.sleep_timer_apply))
                    }
                }

            }

        })
}

@Preview(showBackground = true)
@Composable
fun RadioCountTimerDialogPreview() {
    var sliderPosition by remember { androidx.compose.runtime.mutableFloatStateOf(4.5f) }
    val sliderTimeText = "${sliderPosition.toInt()} min"

    RadioCountTimerDialog(
        modifier = Modifier.background(Color.White),
        onDismiss = { /* No action for preview */ },
        sliderPosition = sliderPosition,
        sliderTimeText = sliderTimeText,
        onBarValueChange = { newPosition -> sliderPosition = newPosition },
        onTimerApply = { /* No action for preview */ },
        onClear = { sliderPosition = 0f } // Reset slider in preview
    )
}
