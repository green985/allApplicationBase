package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionFilterBar
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListEvent
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListScreen
import com.oyetech.composebase.projectQuestionsFeature.theme.QuestionProjectViewAttrs
import com.oyetech.languageModule.keyset.LanguageKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AdminApproveQuestionScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<AdminApproveQuestionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // List VM is injected into AdminApproveQuestionVm
    val listState by vm.questionListVm.listViewState.collectAsStateWithLifecycle()
    val listUiState by vm.questionListVm.uiState.collectAsStateWithLifecycle()

    AdminApproveQuestionScreen(
        uiState = uiState,
        listCount = listState.items.size,
        onEvent = { vm.onEvent(it) },
        listContent = {
            QuestionListScreen(
                contentPadding = it,
                onEvent = { ev ->
                    when (ev) {
                        is QuestionListEvent.OnTagFilterChanged -> {
                            vm.onEvent(
                                AdminApproveQuestionEvent.OnTagFilterChanged(
                                    ev.tag,
                                    ev.adminFilterType
                                )
                            )
                        }

                        else -> vm.questionListVm.onEvent(ev)
                    }
                },
                onQuestionEvent = { ev -> vm.questionListVm.onQuestionEvent(ev) },
                listViewState = listState, uiState = null,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AdminApproveQuestionContent(
    uiState: AdminApproveQuestionUiState,
    listCount: Int,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
    listContent: @Composable (PaddingValues) -> Unit,
) {
    // Define tabs with filter types and localized labels
    val tabs = uiState.tabs

    // Find current tab index
    val currentTabIndex =
        tabs.indexOfFirst { it.first == uiState.currentFilterType }.coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = currentTabIndex,
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(QuestionProjectViewAttrs.spacingMd),
        modifier = Modifier.fillMaxSize()
    ) {
        // PrimaryTabRow for filter selection
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, (filterType, label) ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(label) }
                )
            }
        }

        // Sync pager page change once
        LaunchedEffect(pagerState.currentPage) {
            val (filterType, _) = tabs[pagerState.currentPage]
            onEvent(AdminApproveQuestionEvent.OnFilterSelected(filterType))
        }

        // HorizontalPager for tab content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val (filterType, _) = tabs[page]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(QuestionProjectViewAttrs.paddingPage)
            ) {
                // Total count header
                Text(
                    text = "Total: $listCount questions",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = QuestionProjectViewAttrs.spacingSm)
                )

                // List content area
                listContent(PaddingValues(0.dp))
            }
        }

        // Bottom action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(QuestionProjectViewAttrs.paddingPage),
            horizontalArrangement = Arrangement.spacedBy(QuestionProjectViewAttrs.spacingSm)
        ) {
            Button(
                onClick = { onEvent(AdminApproveQuestionEvent.OnApproveAll) },
                enabled = !uiState.isLoading,
                modifier = Modifier.weight(1f)
            ) { Text(LanguageKey.approveAll) }
            Button(
                onClick = { onEvent(AdminApproveQuestionEvent.OnDeclineAll) },
                enabled = !uiState.isLoading,
                modifier = Modifier.weight(1f)
            ) { Text(LanguageKey.declineAll) }
        }
    }
}

@Composable
private fun AdminApproveQuestionScreen(
    uiState: AdminApproveQuestionUiState,
    listCount: Int,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
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
                QuestionFilterBar(
                    currentFilter = uiState.currentFilter,
                    onEvent = { event ->
                        when (event) {
                            is QuestionListEvent.OnTagFilterChanged -> {
                                onEvent(AdminApproveQuestionEvent.OnTagFilterChanged(event.tag))
                            }

                            else -> {}
                        }
                    },
                    isAdminMode = false
                )



                AdminApproveQuestionContent(
                    uiState = uiState,
                    listCount = listCount,
                    onEvent = onEvent,
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
        listContent = { }
    )
}
