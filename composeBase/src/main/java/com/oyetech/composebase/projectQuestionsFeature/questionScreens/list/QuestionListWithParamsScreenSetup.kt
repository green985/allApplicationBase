package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.PagingMoreError
import com.oyetech.composebase.baseViews.loadingErrors.PagingMoreLoading
import com.oyetech.composebase.helpers.viewProperties.InfiniteListHandler
import com.oyetech.composebase.projectQuestionsFeature.views.questions.BaseQuestionView
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
@Suppress("LongParameterList")
fun QuestionListWithParamsScreenSetup(
    modifier: Modifier = Modifier,
    questionTagId: String? = null,
    adminFilterTypeStr: String? = null,
    isAdminMode: Boolean = false,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    questionListType: String? = null,
    userId: String? = null,
) {
    val vm =
        koinViewModel<QuestionListVm2>(
            key = "QuestionListWithParamsScreenSetup_$questionTagId-$adminFilterTypeStr-$questionListType-$userId"
        )
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val listViewState by vm.listViewState.collectAsStateWithLifecycle()

    LaunchedEffect(questionTagId, adminFilterTypeStr, questionListType, userId) {
        if (questionTagId != null) {
            val tag = com.oyetech.models.questionProject.questionOperation.QuestionTagCatalog
                .createQuestionTagList.find { it.id == questionTagId }
            vm.setTagFilter(tag)
        }

        if (adminFilterTypeStr != null) {
            val filterType = try {
                QuestionListAdminFilterType.valueOf(adminFilterTypeStr)
            } catch (e: Exception) {
                QuestionListAdminFilterType.APPROVED_ADMIN
            }
            vm.setAdminFilter(filterType)
        }

        if (questionListType != null && userId != null) {
            vm.setUserFilter(questionListType, userId)
        }

        if (isAdminMode) {
            vm.onQuestionEvent(QuestionViewEvent.SetAdminMode(true))
        }
    }
    QuestionListWithParamsContent(
        contentPadding = innerPadding,
        listViewState = listViewState,
        onQuestionEvent = vm::onQuestionEvent,
    )
}

@Composable
fun QuestionListWithParamsContent(
    contentPadding: PaddingValues,
    listViewState: GenericListState<QuestionViewUiState>,
    onQuestionEvent: (QuestionViewEvent) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    InfiniteListHandler(lazyListState) {
        listViewState.onLoadMore?.invoke()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (listViewState.isLoadingInitial) {
                LoadingScreenFullSize()
            } else if (listViewState.isErrorInitial) {
                ErrorScreenFullSize(errorMessage = listViewState.errorMessage, withoutAlpha = true)
            } else if (listViewState.isEmptyList) {
                ErrorScreenFullSize(errorMessage = "No questions found", withoutAlpha = true)
            } else if (listViewState.items.isEmpty()) {
                ErrorScreenFullSize(errorMessage = "No questions found", withoutAlpha = true)
            } else {
                LazyColumn(
                    modifier = Modifier.padding(4.dp),
                    state = lazyListState,
                ) {
                    itemsIndexed(
                        items = listViewState.items,
                        key = { i: Int, model: QuestionViewUiState -> model.questionId },
                        itemContent = { index, itemUi ->
                            BaseQuestionView(
                                uiState = itemUi,
                                onEvent = { ev ->
                                    onQuestionEvent.invoke(ev)
                                }
                            )
                            if (index == listViewState.items.lastIndex) {
                                when {
                                    listViewState.isLoadingMore -> {
                                        PagingMoreLoading()
                                    }

                                    listViewState.isErrorMore -> {
                                        PagingMoreError(
                                            errorMessage = listViewState.errorMessage,
                                            onRetry = {
                                                listViewState.onRetryMore?.invoke()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionListWithParamsPreview() {
    QuestionListWithParamsContent(
        contentPadding = PaddingValues(),
        listViewState = GenericListState(items = emptyList<QuestionViewUiState>().toImmutableList()),
    )
}
