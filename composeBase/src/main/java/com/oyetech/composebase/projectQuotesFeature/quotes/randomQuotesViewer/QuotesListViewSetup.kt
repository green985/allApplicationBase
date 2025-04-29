package com.oyetech.composebase.projectQuotesFeature.quotes.randomQuotesViewer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteListUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-16.12.2024-
-23:12-
 **/

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun QuotesWithPagerViewSetup(
    vm: QuotesVM = koinViewModel(),
    navigationRoute: (navigationRoute: String) -> Unit,
) {
    val complexItemListState by vm.complexItemViewState.collectAsStateWithLifecycle()

    val quotesList = complexItemListState.items
    val currentPage by vm.currentPage.collectAsState()
    val pagerState = rememberPagerState(initialPage = currentPage) { quotesList.size }
    val coroutineScope = rememberCoroutineScope()
    val event = { event: QuoteListUiEvent ->
        vm.onEvent(event = event)
    }

    fun previousAction() {
        coroutineScope.launch {
            val currentPage = pagerState.currentPage
            if (currentPage - 1 < 0) {
                return@launch
            }
            pagerState.animateScrollToPage(currentPage - 1)
        }
    }

    fun nextAction() {
        coroutineScope.launch {
            val currentPage = pagerState.currentPage
            if (currentPage + 1 > quotesList.size) {
                return@launch
            }
            pagerState.animateScrollToPage(currentPage + 1)
        }
    }
    Column(modifier = Modifier.heightIn(min = 200.dp)) {
        HorizontalPager(
            state = pagerState,
        ) { page ->

            LaunchedEffect(page) {
                Timber.d("page == $page")
                vm.currentPage.value = page
//                event.invoke(QuoteListUiEvent.LoadMore(page))
//                event.invoke(QuoteListUiEvent.QuoteSeen(quotesList[page]))
            }

            key(quotesList[page]) {
                RandomQuotesSmallView(uiState = quotesList[page], navigationRoute = navigationRoute)
            }
        }

        PagerButtonStyle(
            coroutineScope = coroutineScope,
//            fetchRandomQuotes = { vm.loadMoreItem() },
            pagerState = pagerState,
            previousAction = { previousAction() },
            nextAction = { nextAction() }
        )
    }

}

@Composable
private fun PagerButtonStyle(
    coroutineScope: CoroutineScope,
    fetchRandomQuotes: () -> Unit = {},
    previousAction: () -> Unit = {},
    nextAction: () -> Unit = {},

    pagerState: PagerState,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = previousAction) {
                Icon(
                    imageVector = Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    fetchRandomQuotes()
                    delay(100)
                    pagerState.animateScrollToPage(0)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Next"
                )
            }
            IconButton(onClick = nextAction) {
                Icon(
                    imageVector = Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }
    }
}

@Preview
@Composable
fun QuotesListViewSetupPreview() {
    PagerButtonStyle(
        coroutineScope = rememberCoroutineScope(),
        fetchRandomQuotes = {},
        previousAction = {},
        nextAction = {},
        pagerState = rememberPagerState { 1 }
    )
}

