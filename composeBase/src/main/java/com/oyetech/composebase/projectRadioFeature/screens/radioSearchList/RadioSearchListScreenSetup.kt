package com.oyetech.composebase.projectRadioFeature.screens.radioSearchList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
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
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.LoadableLazyColumnState
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.subs.ItemRadioView
import com.oyetech.composebase.projectRadioFeature.screens.radioSearchList.RadioSearchListEvent.SearchQueryChanged
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.android.awaitFrame
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-10.12.2024-
-15:59-
 **/

@Composable
fun RadioSearchListScreenSetup(
    viewModel: RadioSearchVM = koinViewModel<RadioSearchVM>(),
    navigationRoute: (navigationRoute: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val complexItemViewState by viewModel.complexItemViewState.collectAsStateWithLifecycle()

    val lazyListState = rememberLoadableLazyColumnState(
        onLoadMore = {
            viewModel.handleListEvent(ListUIEvent.LoadMore)
        },
    )
    RadioSearchListScreenScreen(
        searchQuery = uiState.searchQuery,
        items = complexItemViewState.items,
        onSearchQueryChange = { viewModel.onEvent(SearchQueryChanged(it)) },
        lazyListState = lazyListState,
        complexItemViewState = complexItemViewState,
        navigationRoute = navigationRoute,
        expanded = uiState.expanded
    )
}

@SuppressWarnings("LongParameterList", "FunctionNaming")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioSearchListScreenScreen(
    searchQuery: String,
    items: ImmutableList<RadioUIState>,
    onSearchQueryChange: (String) -> Unit,
    expanded: Boolean = false,
    lazyListState: LoadableLazyColumnState,
    complexItemViewState: ComplexItemListState<RadioUIState>,
    navigationRoute: (navigationRoute: String) -> Unit,
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

                LoadableLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    isRefreshing = complexItemViewState.isRefreshing,
                    isErrorInitial = complexItemViewState.isErrorInitial,
                    isEmptyList = complexItemViewState.isEmptyList,
                    isLoadingInitial = complexItemViewState.isLoadingInitial,
                    onRetry = { onSearchQueryChange.invoke(searchQuery) },
                )
                {
                    items(items = items, key = { it.stationuuid }, itemContent = { station ->
                        ItemRadioView(station, {
//                    viewModel.handleRadioEvent(it, viewModel.complexItemViewState)
                        }, navigationRoute = navigationRoute)
                    })
                }
            }
        }
    }


    LaunchedEffect(focusRequester) {
        awaitFrame()
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun RadioSearchListScreenSetupPreview() {
//    RadioSearchListScreenScreen(
//        searchQuery = "urbanitas",
//        searchResults = persistentListOf(),
//        onSearchQueryChange = {},
//    )
}