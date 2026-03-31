package com.oyetech.composebase.sharedViews.floating

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Floating Action Button for creating new questions
 *
 * Displays a FAB with Add icon when user is logged in.
 * Handles navigation to create question screen.
 */
@Composable
fun FloatingAskQuestionBar(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<FloatingAskQuestionBarVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    FloatingAskQuestionBarContent(
        modifier = modifier,
        uiState = uiState,
        onEvent = vm::onEvent
    )
}

@Composable
private fun FloatingAskQuestionBarContent(
    modifier: Modifier = Modifier,
    uiState: FloatingAskQuestionBarUiState,
    onEvent: (FloatingAskQuestionBarEvent) -> Unit,
) {
    if (uiState.isVisible) {
        FloatingActionButton(
            onClick = { onEvent(FloatingAskQuestionBarEvent.OnFabClicked) },
            modifier = modifier,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ask Question"
            )
        }
    }
}
