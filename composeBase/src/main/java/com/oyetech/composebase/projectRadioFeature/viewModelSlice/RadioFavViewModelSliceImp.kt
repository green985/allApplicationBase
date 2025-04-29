package com.oyetech.composebase.projectRadioFeature.viewModelSlice

import com.oyetech.composebase.base.baseGenericList.ComplexItemListState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

/**
Created by Erdi Özbek
-2.12.2024-
-23:44-
 **/

class RadioFavViewModelSliceImp(private val radioDataOperationUseCase: RadioDataOperationUseCase) :
    IRadioFavViewModelSlice {

    override suspend fun observeWithListData(mutableStateFlow: MutableStateFlow<ComplexItemListState<RadioUIState>>) {
        radioDataOperationUseCase.getRadioFavListFlow().onEach { favList ->
            val complexItemListState = mutableStateFlow.value
            Timber.d("FavList: $favList")

            val listtt = complexItemListState.items.map { uiState ->
                var uiStateTmp = uiState
                val founded = favList.find {
                    it.stationUuid == uiState.stationuuid
                }
                if (founded != null) {
                    uiStateTmp = uiState.copy(isFavorite = true)
                } else {
                    uiStateTmp = uiState.copy(isFavorite = false)
                }
                uiStateTmp
            }


            mutableStateFlow.updateState {
                copy(
                    items = listtt.toImmutableList()
                )
            }

        }.collect()
    }

    override fun observeForList(): Flow<List<RadioStationResponseData>> {
        return radioDataOperationUseCase.getRadioFavListFlow().map {
            val radioFavList =
                radioDataOperationUseCase.getRadioWithStationUuidList(it.map { it.stationUuid })
            radioFavList
        }
    }

}