package com.oyetech.composebase.projectRadioFeature.screens.radioListScreen

/**
Created by Erdi Özbek
-6.11.2024-
-00:01-
 **/

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.subs.ItemRadioView
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.DeleteListOperationDialog
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.DeleteListOperationDialogSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems.DeleteList
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems.Sort
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent.BackButtonClick
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarEvent.OnActionButtonClick
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarSetup
import com.oyetech.models.radioProject.enums.RadioListEnums
import com.oyetech.models.radioProject.enums.RadioListEnums.Favorites
import com.oyetech.models.radioProject.enums.RadioListEnums.History
import com.oyetech.models.radioProject.enums.RadioListEnums.Tag
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@SuppressLint("LongParameterList", "CoroutineCreationDuringComposition", "BinaryOperationInTimber")
@Composable
fun RadioListScreenSetup(
    listType: String = RadioListEnums.Top_Click.name,
    tagName: String = "",
    languageName: String = "",
    countryName: String = "",
    toolbarTitle: String = "",
    viewModel: RadioListVM = koinViewModel<RadioListVM>(key = listType + "_" + tagName + "_" + languageName + "_" + countryName) {
        parametersOf(
            listType, tagName, languageName, countryName, toolbarTitle
        )
    },
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val openDeleteListInfoDialog = remember { mutableStateOf(false) }

    val radioToolbarState by viewModel.radioToolbarState.collectAsState()

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
        if (sortTrigger == listType) {
            lazyListState.lazyListState.animateScrollToItem(0)
        }
    }

    val items = complexItemViewState.items


    if (openDeleteListInfoDialog.value) {
        val dialogType: String
        if (listType == History.name) {
            dialogType = DeleteListOperationDialog.Delete_List_History.name
        } else {
            dialogType = DeleteListOperationDialog.Delete_List_And_Favorite.name
        }
        DeleteListOperationDialogSetup(
            dialogType = dialogType,
            onDismiss = {
                openDeleteListInfoDialog.value = false
            },
            onSuccess = {
                viewModel.deleteList()
                openDeleteListInfoDialog.value = false
            }
        )
    }

    BaseScaffold(
        topBarContent = {
            val viewHasToolbar = setOf(
                RadioListEnums.Favorites.name,
                RadioListEnums.Tag.name,
                RadioListEnums.History.name,
                RadioListEnums.Country.name,
                RadioListEnums.Languages.name
            )

            if (listType in viewHasToolbar) {
                // Your logic here {
                RadioToolbarSetup(
                    radioToolbarState,
                    onEvent = { event ->
                        when (event) {
                            is OnActionButtonClick -> {
                                when (event.actionItem) {
                                    else -> {
                                        when (event.actionItem) {
                                            is DeleteList -> {
                                                Timber.d("DeleteList")
                                                if (items.size > 0) {
                                                    openDeleteListInfoDialog.value = true
                                                }
                                            }

                                            is Sort -> {
                                                Timber.d("Sort$event")
                                                viewModel.sortList(listType)
                                            }

                                            else -> {

                                            }
                                        }
                                    }
                                }
                            }

                            is BackButtonClick -> {
                                Timber.d("BackButtonClick")
                                navigationRoute.invoke("back")
                            }
                        }

                    })
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.then(
                if (listType == Favorites.name || listType == Tag.name || listType == RadioListEnums.History.name) {
                    Modifier.padding(paddingValues)
                } else {
                    Modifier
                }
            )
        ) {
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
                    items(items = items, key = { it.stationuuid }, itemContent = { station ->
                        ItemRadioView(station, {
                            viewModel.handleRadioEvent(it, viewModel.complexItemViewState)
                        }, navigationRoute = navigationRoute)
                    })
                },
            )
        }
    }
}

fun navigationToTagList(
    navigationRoute: (navigationRoute: String) -> Unit,
    tag: String,
    toolbarTitle: String? = null,
) {
    navigationRoute.invoke(
        RadioAppProjectRoutes.RadioList.withArgs(
            ScreenKey.listType to Tag.name,
            ScreenKey.tagName to tag,
            ScreenKey.toolbarTitle to if (toolbarTitle.isNullOrBlank()) {
                tag
            } else {
                toolbarTitle
            }
        )
    )
}


