package com.oyetech.composebase.projectQuotesFeature.searchScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.composebase.projectQuotesFeature.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-15.02.2025-
-22:15-
 **/

@Composable
fun QuoteSearchScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<QuoteSearchVM>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val lazyPagingItems = vm.quoteSearchPage.collectAsLazyPagingItems()
    QuoteSearchScreen(
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = { vm.onEvent(QuoteSearchEvent.SearchQueryChanged(it)) },
        expanded = uiState.expanded,
        navigationRoute = navigationRoute,
        items = lazyPagingItems,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteSearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    expanded: Boolean = false,
    navigationRoute: (navigationRoute: String) -> Unit,
    items: LazyPagingItems<QuoteUiState>,
) {

    val focusRequester = remember { FocusRequester() }

    val closeOperation: () -> Unit = {
        navigationRoute.invoke("back")
    }

    Column(Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth(),
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.focusRequester(focusRequester),
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = { onSearchQueryChange.invoke(it) },
                    expanded = expanded,
                    onExpandedChange = { },
//                    placeholder = { Text() },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { closeOperation.invoke() }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    },
                )
            },
            expanded = expanded,
            onExpandedChange = { navigationRoute.invoke("back") },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                BasePagingListScreen(
                    items = items,
                    itemKey = { it.quoteId },
                    onBindItem = {
                        RandomQuotesSmallView(uiState = it)
                    },
                )
            }
        }
    }


    LaunchedEffect(focusRequester) {
        awaitFrame()
        focusRequester.requestFocus()
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun QuoteSearchScreenPreview(

) {

    QuoteSearchScreen(
        searchQuery = "",
        onSearchQueryChange = {},
        expanded = true,
        navigationRoute = {},
        items = flowOf(PagingData.from(listOf(QuoteUiState()))).collectAsLazyPagingItems()
    )

}