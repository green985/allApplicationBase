package com.oyetech.composebase.sharedScreens.quotes.tagList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.helpers.viewProperties.gridItems
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.composebase.sharedScreens.quotes.views.ItemQuoteTagView
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-19.12.2024-
-22:35-
 **/

@Composable
fun QuoteTagListSetup(
    navigationRoute: (navigationRoute: String) -> Unit,
    viewModel: QuoteTagListVM = koinViewModel(),
) {
    val radioToolbarState by viewModel.toolbarState
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
                    gridItems(complexItemViewState.items, 3, itemContent = { model ->
                        ItemQuoteTagView(
                            modifier = Modifier,
                            tagName = model.tagName,
                            onClick = {
                                Timber.d(" Tag Name Clicked ${model.tagName}")
                            })
                    })
                },
            )
        }
    }

}