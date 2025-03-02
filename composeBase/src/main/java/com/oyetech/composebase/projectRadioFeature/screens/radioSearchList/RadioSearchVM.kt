package com.oyetech.composebase.projectRadioFeature.screens.radioSearchList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseList.BaseListViewModel2
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.vm.mapToResponse
import com.oyetech.composebase.projectRadioFeature.screens.radioSearchList.RadioSearchListEvent.ExpandedChange
import com.oyetech.composebase.projectRadioFeature.screens.radioSearchList.RadioSearchListEvent.SearchQueryChanged
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-10.12.2024-
-16:09-
 **/

class RadioSearchVM(
    appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private val radioDataOperationUseCase: RadioDataOperationUseCase,
    private val radioOperationUseCase: RadioOperationUseCase,
    private val radioStationListOperationUseCase: RadioStationListOperationUseCase,
) : BaseListViewModel2<RadioUIState>(appDispatchers) {

    private var searchOperationJobs: Job? = null
    private var searchString = ""

    override val complexItemViewState: MutableStateFlow<ComplexItemListState<RadioUIState>> =
        MutableStateFlow(
            ComplexItemListState(
            )
        )

    val uiState = MutableStateFlow(
        RadioSearchListUiState(
            "",
            persistentListOf(),
            false,
            expanded = true,
            ""
        )
    )

    fun onEvent(event: RadioSearchListEvent) {
        when (event) {
            is SearchQueryChanged -> {
                searchOperation(event.query)
            }

            is ExpandedChange -> {
                uiState.updateState {
                    copy(expanded = event.expanded)
                }
            }
        }
    }

    private fun searchOperation(query: String) {
        uiState.updateState {
            copy(searchQuery = query)
        }

        searchOperationJobs?.cancel()
        searchString = query.ifBlank {
            ""
        }

        searchOperationJobs = viewModelScope.launch(getDispatcherIo()) {
            if (searchString.length > 2) {
                delay(HelperConstant.DEBOUNCE_TIME_LONG)
                complexItemViewState.updateState {
                    copy(isLoadingInitial = true)
                }
                radioStationListOperationUseCase.getStationListWithSearchParams(query)
                    .mapToResponse(
                        radioDataOperationUseCase,
                        radioOperationUseCase
                    ).asResult().collectLatest {
                        it.fold(
                            onSuccess = {
                                complexItemViewState.updateState {
                                    if (it.isEmpty()) {
                                        ComplexItemListState(isEmptyList = true)
                                    } else {
                                        ComplexItemListState(items = it)
                                    }
                                }
                            },
                            onFailure = {
                                complexItemViewState.updateState {
                                    ComplexItemListState(
                                        isErrorInitial = true,
                                        errorMessage = errorMessage
                                    )
                                }
                            }
                        )
                    }

            }

        }

    }
}
