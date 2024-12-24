package com.oyetech.composebase.experimental.commentScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-20.12.2024-
-21:50-
 **/

@Composable
fun CommentScreenSetup() {
    val viewModel = koinViewModel<CommentScreenVM>()
    val radioToolbarState by viewModel.toolbarState
    val commentScreenUiState by viewModel.commentScreenUiState.collectAsStateWithLifecycle()
    val complexItemViewState by viewModel.complexItemViewState.collectAsStateWithLifecycle()
    val lazyListState = rememberLoadableLazyColumnState(
        onLoadMore = {
            viewModel.handleListEvent(ListUIEvent.LoadMore)
        },
    )
    BaseScaffold(
        showTopBar = true,
        topBarContent = {
            RadioToolbarSetup(uiState = radioToolbarState)
        }

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = commentScreenUiState.commentContent,
                onValueChange = {
                    viewModel.onEvent(CommentScreenEvent.UpdateContent(it))
                },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button({
                viewModel.onEvent(CommentScreenEvent.AddComment)
            }) {
                Text("Add Comment")
            }

            LoadableLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                isRefreshing = complexItemViewState.isRefreshing,
                isLoadingInitial = complexItemViewState.isLoadingInitial,
                isErrorInitial = complexItemViewState.isErrorInitial,
                onRetry = { viewModel.handleListEvent(ListUIEvent.Retry) },
                isEmptyList = complexItemViewState.isEmptyList,
                onRefresh = { viewModel.handleListEvent(ListUIEvent.Refresh) },
                errorMessage = complexItemViewState.errorMessage,
                content = {
                    items(
                        items = complexItemViewState.items,
                        key = { it.createdAt.time },
                        itemContent = { itemResponse ->
                            Spacer(Modifier.height(16.dp))
                            Text(text = itemResponse.commentContent)
                            Spacer(Modifier.height(16.dp))
                        })
                },
            )
        }
    }

}