package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuestionsFeature.theme.QuestionProjectViewAttrs
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun AdminApproveQuestionScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<AdminApproveQuestionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    AdminApproveQuestionScreen(
        uiState = uiState,
        onEvent = { event: AdminApproveQuestionEvent -> vm.onEvent(event) },
    )

    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { _ ->
            // future one-shot events
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // no-op for now
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminApproveQuestionToolbar(title: String) {
    TopAppBar(title = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    })
}

@Composable
private fun AdminApproveQuestionContent(
    uiState: AdminApproveQuestionUiState,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(QuestionProjectViewAttrs.spacingMd),
        modifier = Modifier
            .fillMaxSize()
            .padding(QuestionProjectViewAttrs.paddingPage)
    ) {
        Text(text = "Pending: ${uiState.pendingCountText}")
        Button(onClick = { onEvent(AdminApproveQuestionEvent.OnRefreshClicked) }) {
            Text(text = "Refresh")
        }
        Button(
            enabled = !uiState.isLoading,
            onClick = { onEvent(AdminApproveQuestionEvent.OnApproveAllClicked) }) {
            Text(text = "Approve All")
        }
    }
}

@Composable
private fun AdminApproveQuestionScreen(
    uiState: AdminApproveQuestionUiState,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    BaseScaffold(
        topBar = { AdminApproveQuestionToolbar("Admin: Approve Questions") },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                AdminApproveQuestionContent(
                    uiState = uiState,
                    onEvent = onEvent,
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AdminApproveQuestionPreview() {
    AdminApproveQuestionScreen(
        uiState = AdminApproveQuestionUiState(),
        onEvent = {},
    )
}
