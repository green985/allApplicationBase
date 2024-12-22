package com.oyetech.composebase.baseViews.globalLoading.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.baseViews.globalLoading.presentation.GlobalLoadingScreen.backgroudAlpha
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiEvent
import com.oyetech.composebase.baseViews.globalLoading.uiModels.GlobalLoadingUiState

private object GlobalLoadingScreen {
    val backgroudAlpha = 0.7f
}

@Composable
fun GlobalLoadingView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroudAlpha))
            .clickable(
                enabled = false,
                onClick = {}), // Kullanıcı aksiyonlarını bloklamak için
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun GlobalErrorView(
    message: String = "",
    onDismiss: () -> Unit = {},
    onRetry: (() -> Unit),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroudAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = Color.White)
            Button(onClick = onDismiss) {
                Text(text = "Dismiss")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GlobalErrorView(
    uiState: GlobalLoadingUiState.Error = GlobalLoadingUiState.Error(""),
    onEvent: (GlobalLoadingUiEvent) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroudAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = uiState.message, color = Color.White)
            Button(onClick = { onEvent(GlobalLoadingUiEvent.Dismiss) }) {
                Text(text = "Dismiss")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onEvent(GlobalLoadingUiEvent.Retry) }) {
                Text(text = "Retry")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun GlobalLoadingScreenPreview() {
    PartialLoadingScreen(uiState = GlobalLoadingUiState.Loading)
}

@Preview
@Composable
fun GlobalLoadingScreenErrorPreview() {
    PartialLoadingScreen(uiState = GlobalLoadingUiState.Error("Error"))
}