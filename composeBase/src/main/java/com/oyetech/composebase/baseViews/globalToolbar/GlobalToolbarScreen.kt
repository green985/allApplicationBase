package com.oyetech.composebase.baseViews.globalToolbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiEvent
import com.oyetech.composebase.baseViews.globalToolbar.uiModels.GlobalToolbarUiState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalToolbarScreen(
    uiState: GlobalToolbarUiState,
    onEvent: (GlobalToolbarUiEvent) -> Unit = {},
) {
    val isCenterText = uiState.isCenterText
    Surface(
        shadowElevation = 8.dp,
    ) {
        TopAppBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.title,
                    textAlign = if (isCenterText) TextAlign.Center else null
                )
            },
            navigationIcon = {
                if (uiState.showBackButton) {
                    IconButton(onClick = { onEvent(GlobalToolbarUiEvent.OnBackButtonClick) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    null
                }
            },
            actions = {
                if (uiState.actionButtonText != null) {
                    Button(onClick = { onEvent(GlobalToolbarUiEvent.OnActionButtonClick) }) {
                        Text(uiState.actionButtonText)
                    }
                }
            }
        )
    }
}

@Composable
fun initGlobalToolbar() {
    // GlobalToolbarViewModel'ı kullanarak toolbar'ı başlat
    val vm = koinViewModel<GlobalToolbarViewModelWithDelegate>()
    val uiState by vm.uiState.collectAsState()
    GlobalToolbarScreen(uiState = uiState, onEvent = { event ->
        vm.handleEvent(event)
    })
}

// preview yaz
// @Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GlobalToolbarScreenPreview() {
    GlobalToolbarScreen(
        uiState = GlobalToolbarUiState(
            title = "Title",
            showBackButton = true,
            actionButtonText = "Action"
        )
    ) {}
}

@Preview
@Composable
fun GlobalToolbarScreenPreviewWithoutBackButton() {
    GlobalToolbarScreen(
        uiState = GlobalToolbarUiState(
            title = "Title",
            showBackButton = false,
            actionButtonText = "Action"
        )
    ) {}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GlobalToolbarScreenPreviewWithoutBackButtonCenter() {
    GlobalToolbarScreen(
        uiState = GlobalToolbarUiState(
            title = "Title",
            isCenterText = true
        )
    ) {}
}

