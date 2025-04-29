package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.tools.stringHelper.StringHelper.toUniqString
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-3.02.2025-
-22:29-
 **/

@Composable
fun AdviceQuoteDebugScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<AdviceQuoteDebugVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val lazyPagingItems = vm.adviceQuotesPage.collectAsLazyPagingItems()


    AdviceQuoteDebugScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
        lazyPagingItems
    )

}

@Composable
fun AdviceQuoteDebugScreen(
    modifier: Modifier = Modifier,
    uiState: AdviceQuoteDebugUiState,
    onEvent: (AdviceQuoteDebugEvent) -> Unit,
    lazyPagingItems: LazyPagingItems<ItemAdviceQuoteDebugUiState>,
) {

    BaseScaffold {
        Column(modifier = Modifier.padding()) {
            BasePagingListScreen(
                items = lazyPagingItems, // This parameter is abstracted, not used here
                itemKey = { quote -> quote.quoteId.toUniqString() },
                onBindItem = { quote ->
                    AdviceQuoteOperationView(
                        uiState = quote,
                        onEvent = { onEvent.invoke(it) }
                    )
                },
            )
        }
    }

}