package com.oyetech.composebase.baseViews.snackbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import timber.log.Timber

@Composable
fun DefaultSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = { snackbarHostState.currentSnackbarData?.dismiss() },

    ) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                content = {
                    Timber.d("Snackbar message: ${data.visuals.message}")
                    Text(
                        text = data.visuals.message,
                        style = AppTextStyles.body
                    )
                },
                action = {
                    data.visuals.actionLabel?.let { actionLabel ->
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = actionLabel,
                                color = AppColors.primary,
                                style = AppTextStyles.body
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
    )
}
