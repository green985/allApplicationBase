package com.oyetech.composebase.projectRadioFeature.screens.languageList

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.models.radioProject.enums.RadioListEnums.Languages
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-21.11.2024-
-20:55-
 **/

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LanguageListScreenSetup(
    navigationRoute: (navigationRoute: String) -> Unit = {},
    viewModel: LanguageVM = koinViewModel<LanguageVM>(),
) {
    val complexItemViewState by viewModel.complexItemViewState.collectAsStateWithLifecycle()
    val lazyListState = rememberLoadableLazyColumnState(
        onLoadMore = {
            viewModel.handleListEvent(ListUIEvent.LoadMore)
        },
    )
    val scope = rememberCoroutineScope()
    val sortTrigger by viewModel.radioStationListOperationUseCase.getSortOperationClickNavigatorStateFlow()
        .collectAsStateWithLifecycle("")

    // for make scroll to top when sort trigger
    scope.launch {
        if (sortTrigger == Languages.name) {
            lazyListState.lazyListState.animateScrollToItem(0)
        }
    }

    val items = complexItemViewState.items


    BaseScaffold() { paddingValues ->
        Column(modifier = Modifier.padding()) {
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
                    items(items = items, key = { it.languageName }, itemContent = { model ->
                        ItemLanguageView(model, {
                            navigationRoute.invoke(
                                RadioAppProjectRoutes.RadioList.withArgs(
                                    ScreenKey.listType to Languages.name,
                                    ScreenKey.languageName to model.languageName,
                                    ScreenKey.toolbarTitle to model.languageName.plus(" (") + model.radioCount + ")"
                                )
                            )
                        })
                    })
                },
            )
        }
    }

}

@Composable
fun ItemLanguageView(uiState: ItemLanguageListUiState, action: (() -> Unit) = {}) {
    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    action()
                }
                .padding(16.dp),
            verticalAlignment = CenterVertically
        ) {

            Text(
                color = MaterialTheme.colorScheme.primary,
                text = uiState.languageName, style =
                MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                color = MaterialTheme.colorScheme.onBackground,
                text = uiState.radioCount.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        HorizontalDivider(thickness = 1.dp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ItemLanguageViewPreview() {
    ItemLanguageView(
        ItemLanguageListUiState(
            languageName = "Türkçe",
            radioCount = 10
        )
    )
}