package com.oyetech.composebase.projectRadioFeature.screens.tagList

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.helpers.viewProperties.gridItems
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.navigationToTagList
import com.oyetech.composebase.projectRadioFeature.screens.views.ItemTagView
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.models.radioProject.enums.RadioListEnums.Country
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
Created by Erdi Özbek
-14.12.2024-
-18:54-
 **/
@Composable
@SuppressLint("CoroutineCreationDuringComposition")
fun TagListScreenSetup(
    navigationRoute: (navigationRoute: String) -> Unit,
    viewModel: TagListVM = koinViewModel(),
) {
    val radioToolbarState by viewModel.toolbarState
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
        if (sortTrigger == Country.name) {
            lazyListState.lazyListState.animateScrollToItem(0)
            Timber.d("sortTrigger trigledi== $sortTrigger")
        }
    }

    val items = complexItemViewState.items

    Timber.d("itemmmm == " + items.size)

    BaseScaffold(
        showTopBar = true,
        topBarContent = {
            RadioToolbarSetup(uiState = radioToolbarState)
        }

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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

                    gridItems(complexItemViewState.items, 3, itemContent = { model ->
                        ItemTagView(
                            modifier = Modifier,
                            tagName = model.tagName,
                            count = model.radioCount,
                            onClick = {
                                val tag = model.tagName
                                val toolbarTitle = model.tagName.plus(" (") + model.radioCount + ")"
                                navigationToTagList(navigationRoute, tag, toolbarTitle)
                            })
                    })

//
//                    items(items = items, key = { it.tagName }, itemContent = { model ->
//
//                    })
                },
            )
        }
    }

}