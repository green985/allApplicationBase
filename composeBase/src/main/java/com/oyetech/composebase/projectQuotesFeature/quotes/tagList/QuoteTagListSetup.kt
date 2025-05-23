package com.oyetech.composebase.projectQuotesFeature.quotes.tagList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.ListUIEvent.LoadMore
import com.oyetech.composebase.base.baseGenericList.ListUIEvent.Refresh
import com.oyetech.composebase.base.baseGenericList.ListUIEvent.Retry
import com.oyetech.composebase.base.baseGenericList.LoadableLazyColumn
import com.oyetech.composebase.base.baseGenericList.rememberLoadableLazyColumnState
import com.oyetech.composebase.helpers.viewProperties.gridItems
import com.oyetech.composebase.projectQuotesFeature.quotes.views.ItemQuoteTagView
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
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
            viewModel.handleListEvent(LoadMore)
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
                lazyColumnState = lazyListState,
                isRefreshing = complexItemViewState.isRefreshing,
                isLoadingInitial = complexItemViewState.isLoadingInitial,
                isErrorInitial = complexItemViewState.isErrorInitial,
                onRetry = { viewModel.handleListEvent(Retry) },
                isEmptyList = complexItemViewState.isEmptyList,
                onRefresh = { viewModel.handleListEvent(Refresh) },
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