package com.oyetech.composebase.projectRadioFeature.screens.tagList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.R
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.changeSortType
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.repository.helpers.logicRepositories.RadioListSortRepository
import com.oyetech.domain.useCases.remoteUseCase.RadioCountryTagOperationUseCase
import com.oyetech.models.radioEntity.tag.TagResponseData
import com.oyetech.models.radioProject.entity.radioEntity.country.CountryResponseData
import com.oyetech.models.radioProject.enums.RadioListEnums.Country
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
-14.12.2024-
-18:54-
 **/

class TagListVM(
    appDispatchers: AppDispatchers,
    private val radioCountryTagOperationUseCase: RadioCountryTagOperationUseCase,
    private val radioListSortRepository: RadioListSortRepository,
    val radioStationListOperationUseCase: RadioStationListOperationUseCase,
) : BaseListViewModel<TagListUiState>(appDispatchers) {

    val toolbarState = mutableStateOf(
        RadioToolbarState(
            title = context.getString(R.string.action_tags),
        )
    )

    override val complexItemViewState:
            MutableStateFlow<ComplexItemListState<TagListUiState>> = MutableStateFlow(
        ComplexItemListState(errorMessage = errorMessage)
    )

    var lastList: ImmutableList<TagResponseData> = persistentListOf()

    init {
        getCountryList()

        observeSortListAction()
    }

    private fun observeSortListAction() {
        viewModelScope.launch(getDispatcherIo()) {
            radioStationListOperationUseCase.getSortOperationClickNavigatorStateFlow().onEach {
                if (Country.name == it) {
                    sortList()
                }
            }.collect()
        }
    }

    override fun refreshList() {
        complexItemViewState.updateState {
            copy(isRefreshing = true)
        }
        getCountryList(isFromRefresh = true)
    }

    override fun retry() {
        complexItemViewState.updateState {
            ComplexItemListState(
                isLoadingInitial = true
            )
        }
        getCountryList()
    }

    private fun sortList() {
        val itemSortType = complexItemViewState.value.itemSortType
        val sortType = itemSortType.changeSortType()

        val sortedList = radioListSortRepository.sortList(
            lastList.toList(),
            sortType.toString()
        ) as List<CountryResponseData>



        complexItemViewState.updateState {
            copy(
                items = sortedList.map {
                    TagListUiState(
                        tagName = it.name,
                        radioCount = it.stationcount,
                    )
                }.toImmutableList(),
                itemSortType = sortType,
                isLoadingInitial = false,
                isRefreshing = false
            )
        }


    }

    private fun getCountryList(isFromRefresh: Boolean = false) {
        if (!isFromRefresh) {
            complexItemViewState.updateState { copy(isLoadingInitial = true) }
        }
        viewModelScope.launch(getDispatcherIo()) {
            radioCountryTagOperationUseCase.getTagListWithSorted().map { list ->
                lastList = list.toImmutableList()

                list
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

    private fun List<TagResponseData>.mapToUiState(): ImmutableList<TagListUiState> {
        return this.map {
            TagListUiState(
                tagName = it.name,
                radioCount = it.stationcount,
            )
        }.toImmutableList()
    }


}