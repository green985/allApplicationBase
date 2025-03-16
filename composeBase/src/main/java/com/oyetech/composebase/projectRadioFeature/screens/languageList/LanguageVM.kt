package com.oyetech.composebase.projectRadioFeature.screens.languageList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.ComplexItemListState
import com.oyetech.composebase.base.baseGenericList.changeSortType
import com.oyetech.composebase.base.baseList.BaseListViewModel2
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.repository.helpers.logicRepositories.RadioListSortRepository
import com.oyetech.domain.useCases.remoteUseCase.RadioCountryTagOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.language.LanguageResponseData
import com.oyetech.models.radioProject.enums.RadioListEnums.Languages
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-21.11.2024-
-20:54-
 **/
class LanguageVM(
    val appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    val radioCountryTagOperationUseCase: RadioCountryTagOperationUseCase,
    val radioStationListOperationUseCase: RadioStationListOperationUseCase,
    val radioListSortRepository: RadioListSortRepository,
) : BaseListViewModel2<ItemLanguageListUiState>(appDispatchers) {

    override val complexItemViewState: MutableStateFlow<ComplexItemListState<ItemLanguageListUiState>> =
        MutableStateFlow(
            ComplexItemListState()
        )

    var lastList: ImmutableList<LanguageResponseData> = persistentListOf()

    init {
        getLanguageList()

        observeSortListAction()
    }

    private fun observeSortListAction() {
        viewModelScope.launch(getDispatcherIo()) {
            radioStationListOperationUseCase.getSortOperationClickNavigatorStateFlow().onEach {
                if (Languages.name == it) {
                    sortList()
                }
            }.collect()
        }
    }

    private fun sortList() {
        val itemSortType = complexItemViewState.value.itemSortType
        val sortType = itemSortType.changeSortType()

        val sortedList = radioListSortRepository.sortList(
            lastList.toList(),
            sortType.toString()
        ) as List<LanguageResponseData>



        complexItemViewState.value = ComplexItemListState(
            items = sortedList.mapToUiState(),
            itemSortType = sortType
        )
    }

    private fun getLanguageList(isFromRefresh: Boolean = false) {
        if (!isFromRefresh) {
            complexItemViewState.updateState { copy(isLoadingInitial = true) }
        }
        viewModelScope.launch(appDispatchers.io) {
            radioCountryTagOperationUseCase.getLanguagesList().map {
                lastList = it.toImmutableList()


                it
            }.asResult().collectLatest { list1 ->
                list1.fold(
                    onSuccess = { list ->
                        if (list.isEmpty()) {
                            complexItemViewState.value = ComplexItemListState(
                                isEmptyList = true
                            )
                        } else {
                            complexItemViewState.value =
                                ComplexItemListState(
                                    items = list.mapToUiState(),
                                )
                        }

                    },
                    onFailure = {
                        complexItemViewState.value = ComplexItemListState(
                            isErrorInitial = true,
                            errorMessage = errorMessage
                        )
                    }
                )
            }

        }
    }

    override fun refreshList() {
        complexItemViewState.updateState {
            copy(isRefreshing = true)
        }
        getLanguageList(isFromRefresh = true)
    }

    override fun retry() {
        complexItemViewState.updateState {
            ComplexItemListState(
                isLoadingInitial = true
            )
        }
        getLanguageList()
    }
}

private fun List<LanguageResponseData>.mapToUiState(): ImmutableList<ItemLanguageListUiState> {
    return this.map {
        ItemLanguageListUiState(
            languageName = it.languageName,
            radioCount = it.stationcount
        )
    }.toImmutableList()
}
