package com.oyetech.composebase.projectRadioFeature.screens.countryList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.ComplexItemListState
import com.oyetech.composebase.base.baseGenericList.changeSortType
import com.oyetech.composebase.base.baseList.BaseListViewModel2
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.countryList.helper.CountryCodeDictionary
import com.oyetech.composebase.projectRadioFeature.screens.countryList.helper.CountryFlagsLoader
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.repository.helpers.logicRepositories.RadioListSortRepository
import com.oyetech.domain.useCases.remoteUseCase.RadioCountryTagOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.country.CountryResponseData
import com.oyetech.models.radioProject.enums.RadioListEnums.Country
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
class CountryVM(
    val appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    val radioCountryTagOperationUseCase: RadioCountryTagOperationUseCase,
    val radioStationListOperationUseCase: RadioStationListOperationUseCase,
    val radioListSortRepository: RadioListSortRepository,
) : BaseListViewModel2<ItemCountryListUiState>(appDispatchers) {

    override val complexItemViewState: MutableStateFlow<ComplexItemListState<ItemCountryListUiState>> =
        MutableStateFlow(
            ComplexItemListState(errorMessage = errorMessage)
        )

    var countryFlagsLoader = CountryFlagsLoader()
    var countryCodeDictionary = CountryCodeDictionary()

    var lastList: ImmutableList<CountryResponseData> = persistentListOf()

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
                    ItemCountryListUiState(
                        countryName = it.name,
                        countryFullName = it.countryFullName,
                        radioCount = it.stationcount,
                        flagId = it.flagId
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
        viewModelScope.launch(appDispatchers.io) {
            radioCountryTagOperationUseCase.getCountryList().map { list ->
                lastList = list.toImmutableList()
                countryCodeDictionary.load()
                list.forEach {
                    val drawableId = countryFlagsLoader.getFlagResourceId(it.name)
                    it.flagId = drawableId
                    it.countryFullName =
                        countryCodeDictionary.getCountryByCode(it.name) ?: ""
                }

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

    private fun List<CountryResponseData>.mapToUiState(): ImmutableList<ItemCountryListUiState> {
        return this.map {
            ItemCountryListUiState(
                countryName = it.countryFullName,
                countryFullName = it.countryFullName,
                radioCount = it.stationcount,
                flagId = it.flagId
            )
        }.toImmutableList()
    }


}
