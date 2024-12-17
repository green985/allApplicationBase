package com.oyetech.composebase.projectRadioFeature.screens.tabAllList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.countryList.CountryListScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.languageList.LanguageListScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioListScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.models.radioProject.enums.RadioListEnums
import com.oyetech.models.radioProject.enums.RadioListEnums.Country
import com.oyetech.models.radioProject.enums.RadioListEnums.Languages
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

// Composable Function for Scrollable Tab Row
@Suppress("FunctionName")
@Composable
fun TabAllListScreenSetup(
    navigationRoute: (navigationRoute: String) -> Unit = {},
    viewModel: RadioAllListFragmentVM = koinViewModel<RadioAllListFragmentVM>(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val radioToolbarState by viewModel.radioToolbarState.collectAsState()

    val pagerState = rememberPagerState { uiState.tabEnumList.size }
    BaseScaffold(
        showTopBar = true,
        topBarContent = {
            RadioToolbarSetup(
                radioToolbarState,
                onEvent = {
                    viewModel.handleToolbarAction(
                        it,
                        uiState.tabEnumList[pagerState.currentPage]
                    )
                    if (it is RadioToolbarEvent.OnActionButtonClick) {
                        when (it.actionItem) {
                            is RadioToolbarActionItems.Timer -> {
                                navigationRoute.invoke(RadioAppProjectRoutes.TimerDialog.route)
                            }

                            is RadioToolbarActionItems.Search -> {
                                navigationRoute.invoke(RadioAppProjectRoutes.RadioSearchList.route)
                            }

                            else -> {

                            }
                        }
                    }
                })
        }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
//            QuotesListViewSetup()
            Spacer(modifier = Modifier.height(8.dp))
            TabAllScreenView(
                pagerState = pagerState,
                tabEnums = uiState.tabEnumList, uiState.tabNameList,
                navigationRoute = navigationRoute
            )
        }
    }

}

@Composable
private fun TabAllScreenView(
    pagerState: PagerState,
    tabEnums: ImmutableList<RadioListEnums>,
    tabNameList: ImmutableList<String>,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        // Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            tabEnums.forEachIndexed { index, _ ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tabNameList[index],
                            style = MaterialTheme.typography.labelLarge,
                            color = if (pagerState.currentPage == index) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        )
                    }
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            key = { tabEnums[it] }
        ) { page ->
            Timber.d("page == $page")
            key(tabEnums[page]) {
                when (tabEnums[page]) {
                    Languages -> {
                        LanguageListScreenSetup(navigationRoute)
                        Timber.d("Languages")
                    }

                    Country -> {
                        CountryListScreenSetup(navigationRoute)
                        Timber.d("Languages")
                    }

                    else -> {
                        Timber.d("else")
                        RadioListScreenSetup(tabEnums[page].name, navigationRoute = navigationRoute)
                    }
                }

            }
        }
    }
}

@Composable
@Preview
fun TabAllListScreenPreview() {
    TabAllScreenView(
        pagerState = rememberPagerState(pageCount = { 2 }),
        RadioListEnums.entries.toTypedArray().toImmutableList(),
        tabNameList = persistentListOf("Deneme", "Bir"),
    )


}