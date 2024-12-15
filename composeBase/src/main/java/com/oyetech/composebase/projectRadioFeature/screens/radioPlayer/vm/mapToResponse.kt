package com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.vm

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.mapToUiModel
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

context(BaseViewModel)
@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<List<RadioStationResponseData>>.mapToResponse(
    radioDataOperationUseCase: RadioDataOperationUseCase,
    radioOperationUseCase: RadioOperationUseCase,
): Flow<ImmutableList<RadioUIState>> {
    return this.flatMapConcat {
        // map too radioResponse...
        withContext(getDispatcherIo()) {

            val latestRadioData = radioDataOperationUseCase.getLastRadioData()
            val latestRadioState = radioOperationUseCase.getPlayerState()
            val latestFavList = radioDataOperationUseCase.getRadioFavList().map {
                it.stationUuid
            }

            val newList = arrayListOf<RadioUIState>()
            it.forEach {
                var radioModel = it.mapToUiModel()
                if (radioModel.stationuuid == latestRadioData?.stationuuid) {
                    radioModel = radioModel.copy(isSelectedView = true)
                    if (latestRadioState == Playing) {
                        radioModel = radioModel.copy(isPlayingView = true)
                    }
                }
                latestFavList.forEach { favModel ->
                    if (radioModel.stationuuid == favModel) {
                        radioModel = radioModel.copy(isFavorite = true)
                    }
                }
                newList.add(radioModel)
            }
            val immutableList = newList.toImmutableList()
            flowOf(immutableList)
        }

    }
}