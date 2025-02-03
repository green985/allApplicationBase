package com.oyetech.composebase.projectQuotesFeature.homeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuotesFeature.quotes.listScreen.QuoteListScreenSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarActionItems
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.models.quotes.enums.QuoteListEnum
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-6.01.2025-
-23:02-
 **/

@Composable
fun QuotesHomeScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val viewModel = koinViewModel<QuotesHomeScreenVm>()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val radioToolbarState by viewModel.radioToolbarState.collectAsState()

    val pagerState = rememberPagerState(
        pageCount = { uiState.tabEnumList.size })
    BaseScaffold(
        showTopBar = true,
        topBarContent = {
            QuoteToolbarSetup(
                radioToolbarState,
                onEvent = {
                    viewModel.handleToolbarAction(
                        it,
                        uiState.tabEnumList[pagerState.currentPage]
                    )
                    if (it is QuoteToolbarEvent.OnActionButtonClick) {
                        when (it.actionItem) {
                            is QuoteToolbarActionItems.Timer -> {
                                navigationRoute.invoke(RadioAppProjectRoutes.TimerDialog.route)
                            }

                            is QuoteToolbarActionItems.Search -> {
                                navigationRoute.invoke(RadioAppProjectRoutes.RadioSearchList.route)
                            }

                            else -> {

                            }
                        }
                    }
                })
        }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
//            Spacer(modifier = Modifier.height(8.dp))

            QuotesHomeView(
                pagerState = pagerState,
                tabEnums = uiState.tabEnumList.toImmutableList(),
                tabNameList = uiState.tabEnumList.map { it.name }.toImmutableList(),
                navigationRoute = navigationRoute
            )
        }
    }


}

@Composable
private fun QuotesHomeView(
    pagerState: PagerState,
    tabEnums: ImmutableList<QuoteListEnum>,
    tabNameList: ImmutableList<String>,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        // Scrollable Tab Row
//        ScrollableTabRow(
//            selectedTabIndex = 0,
//            modifier = Modifier.fillMaxWidth(),
//            edgePadding = 0.dp
//        ) {
//            tabEnums.forEachIndexed { index, _ ->
//                Tab(
//                    selected = pagerState.currentPage == index,
//                    onClick = {
//                        coroutineScope.launch {
//                            pagerState.animateScrollToPage(index)
//                        }
//                    }
//                ) {
//                    Row(
//                        modifier = Modifier.padding(8.dp),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = tabNameList[index],
//                            style = MaterialTheme.typography.labelLarge,
//                            color = if (pagerState.currentPage == index) {
//                                MaterialTheme.colorScheme.primary
//                            } else {
//                                MaterialTheme.colorScheme.onBackground
//                            }
//                        )
//                    }
//                }
//            }
//        }
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier.weight(1f),
//            key = { tabEnums[it] }
//        ) { page ->
//            Timber.d("page == $page")
//            key(tabEnums[page]) {
//                when (tabEnums[page]) {
//                    All -> {
//                        QuoteListScreenSetup(navigationRoute)
//                        Timber.d("Quotes")
//                    }
//
//                    Random -> {
//                        LanguageListScreenSetup(navigationRoute)
//                        Timber.d("Languages")
//                    }
//                }
//
//            }
        QuoteListScreenSetup(navigationRoute)
    }
//    }
}

@Composable
@Preview
fun TabAllListScreenPreview() {

    QuotesHomeView(
        pagerState = rememberPagerState { 0 },
        tabEnums = QuoteListEnum.values().toImmutableList(),
        tabNameList = QuoteListEnum.values().map { it.name }.toImmutableList()
    )


}
