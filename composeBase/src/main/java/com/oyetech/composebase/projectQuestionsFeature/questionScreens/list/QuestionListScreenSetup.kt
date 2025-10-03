package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionYesNoView
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuestionListScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuestionListVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val listViewState by vm.listViewState.collectAsStateWithLifecycle()

    BaseScaffold(
        topBar = { QuestionListToolbar(uiState.toolbarTitleText) },
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            QuestionListScreen(
                contentPadding = innerPadding,
                onEvent = { vm.onEvent(it) },
                onQuestionEvent = { vm.onQuestionEvent(it) },
                listViewState = listViewState,
            )

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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionListToolbar(title: String) {
    TopAppBar(title = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    })
}

@Composable
fun QuestionListScreen(
    contentPadding: PaddingValues,
    onEvent: (QuestionListEvent) -> Unit,
    onQuestionEvent: (QuestionViewEvent) -> Unit = {},
    listViewState: GenericListState<QuestionViewUiState>,
) {
    val lazyListState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.padding(4.dp),
                state = lazyListState,
            ) {
                items(
                    items = listViewState.items,
                    key = { it.questionId },
                    itemContent = { itemUi ->
                        QuestionYesNoView(
                            uiState = itemUi,
                            onEvent = { ev ->
                                onQuestionEvent.invoke(ev)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionListPreview() {
    QuestionListScreen(
        contentPadding = PaddingValues(),
        onEvent = {},
        listViewState = GenericListState(items = emptyList<QuestionViewUiState>().toImmutableList()),
    )
}
