package com.oyetech.composebase.sharedViews.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import org.koin.compose.koinInject

@Composable
fun FloatingAskQuestionBar(
    onClick: () -> Unit,
) {
    val loginOperationVM = koinInject<LoginOperationVM>()
    val loginUiState by loginOperationVM.loginOperationState.collectAsState()

    if (loginUiState.isLogin) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Ask Question")
        }
    }
}
