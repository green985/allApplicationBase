package com.oyetech.composebase.projectRadioFeature.screens.radioListScreen

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.R
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.changeSortType
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.vm.mapToResponse
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarActionItems.DeleteList
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.composebase.projectRadioFeature.viewModelSlice.IRadioFavViewModelSlice
import com.oyetech.composebase.projectRadioFeature.viewModelSlice.IRadioPlayerViewModelSlice
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.repository.helpers.logicRepositories.RadioListSortRepository
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.enums.RadioListEnums.Country
import com.oyetech.models.radioProject.enums.RadioListEnums.Favorites
import com.oyetech.models.radioProject.enums.RadioListEnums.History
import com.oyetech.models.radioProject.enums.RadioListEnums.Languages
import com.oyetech.models.radioProject.enums.RadioListEnums.Tag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

/**
Created by Erdi Özbek
-11.11.2024-
-13:42-
 **/

@Suppress("LongParameterList")
class RadioListVM(
    val listType: String,
    val tagName: String,
    val languageName: String,
    val countryName: String,
    val toolbarTitle: String,
    private val dispatchers: AppDispatchers,
    val radioDataOperationUseCase: RadioDataOperationUseCase,
    val radioOperationUseCase: RadioOperationUseCase,
    val radioPlayerViewModelSlice: IRadioPlayerViewModelSlice,
    val radioFavViewModelSlice: IRadioFavViewModelSlice,
    val radioListSortRepository: RadioListSortRepository,
) : BaseListViewModel<RadioUIState>(dispatchers) {
    val radioToolbarState: MutableStateFlow<RadioToolbarState>
        get() {
            return if (listType == Favorites.name) {
                MutableStateFlow(
                    RadioToolbarState(
                        title = context.getString(R.string.nav_item_starred),
                        actionButtonState = persistentListOf(
                            DeleteList(R.drawable.ic_delete)
                        )
                    )
                )
            } else if (listType == History.name) {
                MutableStateFlow(
                    RadioToolbarState(
                        title = context.getString(R.string.nav_item_history),
                        actionButtonState = persistentListOf(
                            DeleteList(R.drawable.ic_delete)
                        )
                    )
                )
            } else if (listType == Country.name || listType == Languages.name || listType == Tag.name) {
                MutableStateFlow(
                    RadioToolbarState(
                        title = toolbarTitle,
                        showBackButton = true,
                        actionButtonState = persistentListOf(
                            RadioToolbarActionItems.Sort(R.drawable.ic_sort)
                        )
                    )
                )
            } else {
                MutableStateFlow(RadioToolbarState())
            }
        }
    override val complexItemViewState: MutableStateFlow<ComplexItemListState<RadioUIState>> =
        MutableStateFlow<ComplexItemListState<RadioUIState>>(
            ComplexItemListState(
                items = persistentListOf(),
                isLoadingInitial = true
            )
        )
    var lastList: ImmutableList<RadioStationResponseData> = persistentListOf()

    var listHeaderString = ""
    var radioListType = listType
    lateinit var flowable: Flow<List<RadioStationResponseData>>
    lateinit var radioUIStateFlow: Flow<List<RadioUIState>>

    val radioStationListOperationUseCase: RadioStationListOperationUseCase by KoinJavaComponent.inject(
        RadioStationListOperationUseCase::class.java
    )

    init {
        getList()

        viewModelScope.launch(getDispatcherIo()) {
            radioFavViewModelSlice.observeWithListData(complexItemViewState)
        }

        viewModelScope.launch(getDispatcherIo()) {
            radioOperationUseCase.radioViewStateNewMutableStateFlow.collectLatest {
                val findListItem = complexItemViewState.value.items.find { item ->
                    item.stationuuid == it.data?.stationuuid
                }
                findListItem?.let {
                    complexItemViewState.updateState {
                        copy(
                            items = items.map { item ->
                                if (item.stationuuid == it.stationuuid) {
                                    it.copy(isSelectedView = true)
                                } else {
                                    item.copy(isSelectedView = false)
                                }
                            }.toImmutableList()
                        )
                    }
                }
            }
        }

        observeSortListAction()
    }

    private fun observeSortListAction() {
        viewModelScope.launch(dispatchers.io) {
            radioStationListOperationUseCase.getSortOperationClickNavigatorStateFlow().onEach {
                if (listType == it) {
                    sortList()
                }
            }.collect()
        }
    }

    private fun sortList() {
        val itemSortType = complexItemViewState.value.itemSortType
        val sortType = itemSortType.changeSortType()

        val sortedList = radioListSortRepository.sortList(lastList.toList(), sortType.toString())


        viewModelScope.launch(getDispatcherIo()) {
            flowOf(sortedList as List<RadioStationResponseData>).mapToResponse(
                radioDataOperationUseCase,
                radioOperationUseCase
            ).collect {
                complexItemViewState.updateState {
                    ComplexItemListState(items = it.toImmutableList(), itemSortType = sortType)
                }

            }
        }
    }

    fun handleRadioEvent(
        it: RadioUIEvent,
        complexItemViewState: MutableStateFlow<ComplexItemListState<RadioUIState>>?,
    ) {
        radioPlayerViewModelSlice.handleRadioEvent(it, complexItemViewState)
    }

    fun deleteList() {
        if (listType == Favorites.name) {
            radioDataOperationUseCase.clearFavList()
        } else if (listType == History.name) {
            radioDataOperationUseCase.clearHistoryList()
        }
    }

    fun sortList(listType: String) {
        viewModelScope.launch {
            radioStationListOperationUseCase.triggerSortOperationClickNavigator(listType)
        }
    }

    private fun getList(isFromRefresh: Boolean = false) {
        setFlowableWithListType()
        if (!isFromRefresh) {
            complexItemViewState.updateState { copy(isLoadingInitial = true) }
        }
        viewModelScope.launch(getDispatcherIo()) {
            radioUIStateFlow.asResult().collectLatest { list1 ->
                list1.fold(
                    onSuccess = { list ->
                        if (list.isEmpty()) {
                            complexItemViewState.value = ComplexItemListState(
                                isEmptyList = true
                            )
                        } else {
                            complexItemViewState.value =
                                ComplexItemListState(
                                    items = list.toImmutableList(),
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
        getList(isFromRefresh = true)
    }

    override fun retry() {
        complexItemViewState.updateState {
            ComplexItemListState(
                isLoadingInitial = true
            )
        }
        getList()
    }
}