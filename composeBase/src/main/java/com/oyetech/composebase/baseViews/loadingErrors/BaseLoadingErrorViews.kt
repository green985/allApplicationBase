package com.oyetech.composebase.baseViews.loadingErrors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.helpers.ProjectUtil
import com.oyetech.composebase.helpers.errorHelper.toErrorMessage
import com.oyetech.composebase.helpers.viewProperties.DialogHelper
import com.oyetech.composebase.projectRadioFeature.RadioDimensions

/**
Created by Erdi Özbek
-28.12.2024-
-13:40-
 **/
@Suppress("FunctionName")
@Composable
fun LoadingScreenFullSize(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = ProjectUtil.loadingBackgroudAlpha))
            .clickable(
                enabled = false,
                onClick = {}), // Kullanıcı aksiyonlarını bloklamak için
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }
}

@Suppress("FunctionName")

// also is firing when there is no more data
@Composable
fun PagingMoreError(errorMessage: String = "Loading Error", onRetry: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(RadioDimensions.listLoadingItemHeight)
            .background(color = MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = errorMessage.toErrorMessage(),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(8.dp))
        IconButton(onClick = onRetry) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Suppress("FunctionName")

@Composable
fun PagingMoreLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(RadioDimensions.listLoadingItemHeight)
            .background(MaterialTheme.colorScheme.background.copy(alpha = ProjectUtil.loadingBackgroudAlpha))
            .verticalScroll(rememberScrollState())
            .clickable(
                enabled = false,
                onClick = {}), // Kullanıcı aksiyonlarını bloklamak için
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
    }
}

@Suppress("FunctionName")

@Composable
fun LoadingDialogFullScreen(
) {
    androidx.compose.ui.window.Dialog(
        properties = DialogHelper.fullScreenDialogProperties,
        onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = ProjectUtil.loadingBackgroudAlpha)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
        }
    }

}

@Suppress("FunctionName")

@Composable
fun ErrorDialogFullScreen(
    errorMessage: String = "An error occurred.",
    onDismiss: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
) {
    androidx.compose.ui.window.Dialog(
        properties = DialogHelper.fullScreenDialogProperties,
        onDismissRequest = { onDismiss?.invoke() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = ProjectUtil.backgroudAlpha)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = errorMessage.toErrorMessage(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (onDismiss != null) {
                    Spacer(modifier = Modifier.height(64.dp))
                    TextButton(onClick = onDismiss) {
                        Text(text = "Dismiss")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                if (onRetry != null) {
                    Button(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Deprecated("Use ErrorDialogFullScreen instead")
@Suppress("FunctionName")
@Composable
fun ErrorScreenFullSize(
    errorMessage: String = "",
    onDismiss: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = ProjectUtil.backgroudAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = errorMessage.toErrorMessage(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            if (onDismiss != null) {
                Spacer(modifier = Modifier.height(64.dp))
                Button(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (onRetry != null) {
                Button(onClick = onRetry) {
                    Text(text = "Retry")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
