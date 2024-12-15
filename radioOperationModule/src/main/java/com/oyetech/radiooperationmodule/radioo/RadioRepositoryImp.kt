package com.oyetech.radiooperationmodule.radioo

import com.oyetech.domain.repository.contentOperation.RadioOperationRepository
import com.oyetech.domain.useCases.contentOperations.ExoPlayerOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData

/**
Created by Erdi Özbek
-15.11.2022-
-17:54-
 **/

class RadioRepositoryImp(
    private var exoPlayerOperationUseCase: ExoPlayerOperationUseCase,
) : RadioOperationRepository {

    var lastRadioDataModel: RadioStationResponseData? = RadioStationResponseData()

    override fun startPlayer(radioModel: RadioStationResponseData?) {
        this.lastRadioDataModel = radioModel
        exoPlayerOperationUseCase.startPlayer(radioModel)
    }

    override fun startPlayerForAlarm(radioModel: RadioStationResponseData?) {
        this.lastRadioDataModel = radioModel
        exoPlayerOperationUseCase.startPlayerForAlarm(radioModel)
    }

    override fun startPlayer() {
        exoPlayerOperationUseCase.startPlayer(lastRadioDataModel)
    }

    override fun stopPlayer() {
        exoPlayerOperationUseCase.stopPlayer()
    }

    override fun pausePlayer() {
        exoPlayerOperationUseCase.pausePlayer()
    }

    override fun resumePlayer() {
        exoPlayerOperationUseCase.resumePlayer()
    }

    override fun setVolume(fullVolume: Float) {
        exoPlayerOperationUseCase.setVolume(fullVolume)
    }
}