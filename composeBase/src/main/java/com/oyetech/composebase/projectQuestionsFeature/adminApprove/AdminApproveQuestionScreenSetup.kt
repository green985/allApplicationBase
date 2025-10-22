package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListWithParamsScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.theme.QuestionProjectViewAttrs
import com.oyetech.composebase.sharedViews.app.ApplicationLogoPlaceholder
import com.oyetech.languageModule.keyset.LanguageKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun AdminApproveQuestionScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<AdminApproveQuestionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // List VM is injected into AdminApproveQuestionVm

    AdminApproveQuestionScreen(
        uiState = uiState,
        onEvent = { vm.onEvent(it) },
    )

    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { _ ->
            // future one-shot events
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // no-op for now
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminApproveQuestionToolbar(title: String) {
    TopAppBar(title = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AdminApproveQuestionContent(
    uiState: AdminApproveQuestionUiState,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    // Define tabs with filter types and localized labels
    val tabs = uiState.tabs

    // Find current tab index
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(QuestionProjectViewAttrs.spacingMd),
        modifier = Modifier.fillMaxSize()
    ) {
        // PrimaryTabRow for filter selection
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, (filterType, label) ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(label) }
                )
            }
        }

        // HorizontalPager for tab content
        HorizontalPager(
            state = pagerState,
            key = { tabs[it].first },
            modifier = Modifier.weight(1f)
        ) { page ->
            val (filterType, _) = tabs[page]
            Timber.d("Displaying page: $page")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(QuestionProjectViewAttrs.paddingPage)
            ) {
                if (pagerState.settledPage == page) {
                    QuestionListWithParamsScreenSetup(
                        adminFilterTypeStr = filterType.name,
                        isAdminMode = true
                    )
                } else {
                    ApplicationLogoPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun AdminApproveQuestionScreen(
    uiState: AdminApproveQuestionUiState,
    onEvent: (AdminApproveQuestionEvent) -> Unit,
) {
    BaseScaffold(
        topBar = { AdminApproveQuestionToolbar(LanguageKey.adminApproveScreen) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                AdminApproveQuestionContent(
                    uiState = uiState,
                    onEvent = onEvent,
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AdminApproveQuestionPreview() {
    AdminApproveQuestionScreen(
        uiState = AdminApproveQuestionUiState(),
        onEvent = {},
    )
}
