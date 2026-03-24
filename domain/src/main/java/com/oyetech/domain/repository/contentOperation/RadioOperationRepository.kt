package com.oyetech.domain.repository.contentOperation

import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData

/**
Created by Erdi Özbek
-15.11.2022-
-17:31-
 **/

interface RadioOperationRepository {

    fun startPlayer(radioModel: RadioStationResponseData? = null)

    fun startPlayer()

    fun stopPlayer()

    fun pausePlayer()

    fun resumePlayer()

    fun setVolume(fullVolume: Float)
}
