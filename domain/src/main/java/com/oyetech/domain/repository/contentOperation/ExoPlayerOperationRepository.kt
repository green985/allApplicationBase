package com.oyetech.domain.repository.contentOperation

import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData

/**
Created by Erdi Özbek
-15.11.2022-
-17:33-
 **/

interface ExoPlayerOperationRepository {

    fun startPlayer(radioModel: RadioStationResponseData? = null)

    fun startPlayerForAlarm(radioModel: RadioStationResponseData? = null)

    fun pause()

    fun stop()

    fun resumePlayer()

    fun setVolume(volume: Float)

    // fun isPlaying(): Boolean

}