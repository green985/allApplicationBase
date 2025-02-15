package com.oyetech.composebase.projectQuotesFeature.searchScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.composebase.base.BaseScaffold
import kotlinx.coroutines.android.awaitFrame

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
//    val vm = koinViewModel<QuoteSearchVm>()
//
//    val uiState by vm.uiState.collectAsStateWithLifecycle()

    BaseScaffold {
        Column(modifier = Modifier.padding()) {

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteSearchScreen(
    searchQuery: String,
//    items: ImmutableList<RadioUIState>,
    onSearchQueryChange: (String) -> Unit,
    expanded: Boolean = false,
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
// todo search result will add
//                LoadableLazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    state = lazyListState,
//                    isRefreshing = complexItemViewState.isRefreshing,
//                    isErrorInitial = complexItemViewState.isErrorInitial,
//                    isEmptyList = complexItemViewState.isEmptyList,
//                    isLoadingInitial = complexItemViewState.isLoadingInitial,
//                    onRetry = { onSearchQueryChange.invoke(searchQuery) },
//                )
//                {
//                    items(items = items, key = { it.stationuuid }, itemContent = { station ->
//                        ItemRadioView(station, {
////                    viewModel.handleRadioEvent(it, viewModel.complexItemViewState)
//                        }, navigationRoute = navigationRoute)
//                    })
//                }
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
        navigationRoute = {}
    )

}