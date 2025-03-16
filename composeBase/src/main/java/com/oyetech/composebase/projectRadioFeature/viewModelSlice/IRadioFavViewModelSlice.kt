package com.oyetech.composebase.projectRadioFeature.viewModelSlice

import com.oyetech.composebase.base.baseGenericList.ComplexItemListState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-2.12.2024-
-23:51-
 **/

interface IRadioFavViewModelSlice {
    suspend fun observeWithListData(mutableStateFlow: MutableStateFlow<ComplexItemListState<RadioUIState>>)
    fun observeForList(): Flow<List<RadioStationResponseData>>
}