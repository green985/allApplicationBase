package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
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

    // List VM for moderation operations
    val listVm =
        koinViewModel<com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListVm>()
    val listState by listVm.listViewState.collectAsStateWithLifecycle()

    AdminApproveQuestionScreen(
        uiState = uiState,
        listCount = listState.items.size,
        onEvent = { event: AdminApproveQuestionEvent -> vm.onEvent(event) },
        onFilterSelected = { listVm.setFilter(it) },
        onApproveAll = { listVm.approveAllPending() },
        onDeclineAll = { listVm.declineAllPending() },
        listContent = {
            com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListScreen(
                contentPadding = it,
                onEvent = {},
                onQuestionEvent = { ev -> listVm.onQuestionEvent(ev) },
                listViewState = listState,
            )
        }
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
    listCount: Int,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
    onFilterSelected: (com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListFilterType) -> Unit,
    onApproveAll: () -> Unit,
    onDeclineAll: () -> Unit,
    listContent: @Composable (PaddingValues) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(QuestionProjectViewAttrs.spacingMd),
        modifier = Modifier
            .fillMaxSize()
            .padding(QuestionProjectViewAttrs.paddingPage)
    ) {
        // Tabs for filters
        val tabs = listOf(
            com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListFilterType.ALL to "All",
            com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListFilterType.PENDING to "Pending",
            com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListFilterType.APPROVED to "Approved",
            com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListFilterType.DECLINED to "Declined",
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(QuestionProjectViewAttrs.spacingSm)
        ) {
            tabs.forEach { (type, label) ->
                Button(onClick = { onFilterSelected(type) }) { Text(label) }
            }
        }

        // Total count header
        Text(text = "Total: ${'$'}listCount")

        // List content area
        listContent(PaddingValues(0.dp))

        // Bottom action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = QuestionProjectViewAttrs.spacingMd),
            horizontalArrangement = Arrangement.spacedBy(QuestionProjectViewAttrs.spacingSm)
        ) {
            Button(onClick = onApproveAll, enabled = !uiState.isLoading) { Text("Approve All") }
            Button(onClick = onDeclineAll, enabled = !uiState.isLoading) { Text("Decline All") }
        }
    }
}

@Composable
private fun AdminApproveQuestionScreen(
    uiState: AdminApproveQuestionUiState,
    listCount: Int,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
    onFilterSelected: (com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListFilterType) -> Unit,
    onApproveAll: () -> Unit,
    onDeclineAll: () -> Unit,
    listContent: @Composable (PaddingValues) -> Unit,
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
                    listCount = listCount,
                    onEvent = onEvent,
                    onFilterSelected = onFilterSelected,
                    onApproveAll = onApproveAll,
                    onDeclineAll = onDeclineAll,
                    listContent = { inner -> listContent(inner) },
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
        listCount = 0,
        onEvent = {},
        onFilterSelected = {},
        onApproveAll = {},
        onDeclineAll = {},
        listContent = { }
    )
}
