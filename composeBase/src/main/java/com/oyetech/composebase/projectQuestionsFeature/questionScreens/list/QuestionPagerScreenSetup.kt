package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuestionsFeature.theme.QuestionProjectViewAttrs
import com.oyetech.composebase.projectQuestionsFeature.views.questions.BaseQuestionView
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.sharedViews.app.ApplicationLogoPlaceholder
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun QuestionPagerScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuestionPagerVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val listViewState by vm.questionListVm.listViewState.collectAsStateWithLifecycle()

    BaseScaffold(
        topBar = { QuestionPagerToolbar("Questions") },
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            QuestionPagerScreen(
                contentPadding = innerPadding,
                uiState = uiState,
                listViewState = listViewState,
                onPagerEvent = { vm.onEvent(it) },
                onQuestionEvent = { vm.questionListVm.onQuestionEvent(it) },
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionPagerToolbar(title: String) {
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
fun QuestionPagerScreen(
    contentPadding: PaddingValues,
    uiState: QuestionPagerUiState,
    listViewState: GenericListState<QuestionViewUiState>,
    onPagerEvent: (QuestionPagerEvent) -> Unit,
    onQuestionEvent: (QuestionViewEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        val tabs = uiState.tabs
        // Find current tab index
        val pagerState = rememberPagerState(
            initialPage = 0,
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

            // HorizontalPager for tab content
            HorizontalPager(
                state = pagerState,
                key = { tabs[it].first.id },
                modifier = Modifier.weight(1f)
            ) { page ->
                val (queTag, _) = tabs[page]
                Timber.d("Displaying page: $page")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(QuestionProjectViewAttrs.paddingPage)
                ) {
                    if (pagerState.settledPage == page) {
                        QuestionListWithParamsScreenSetup(questionTagId = queTag.id)
                    } else {
                        ApplicationLogoPlaceholder()
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionListContent(
    listViewState: GenericListState<QuestionViewUiState>,
    onQuestionEvent: (QuestionViewEvent) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(4.dp),
            state = lazyListState,
        ) {
            items(
                items = listViewState.items,
                key = { it.questionId },
                itemContent = { itemUi ->
                    BaseQuestionView(
                        uiState = itemUi,
                        onEvent = { ev ->
                            onQuestionEvent.invoke(ev)
                        }
                    )
                }
            )
        }


        if (listViewState.isLoadingInitial) {
            LoadingScreenFullSize()
        }
        if (listViewState.isErrorInitial) {
            ErrorScreenFullSize(errorMessage = listViewState.errorMessage, withoutAlpha = true)
        }
        if (listViewState.isEmptyList) {
            ErrorScreenFullSize(errorMessage = "No questions found", withoutAlpha = true)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionPagerPreview() {
    QuestionPagerScreen(
        contentPadding = PaddingValues(),
        uiState = QuestionPagerUiState(),
        listViewState = GenericListState(items = emptyList<QuestionViewUiState>().toImmutableList()),
        onPagerEvent = {},
        onQuestionEvent = {},
    )
}
