package com.oyetech.exoplayermodule.analytics

import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime
import com.oyetech.domain.repository.firebase.RadioAnalyticsOperationRepository
import com.oyetech.models.radioProject.entity.firebase.RadioPlayingAnalyticsData
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import timber.log.Timber

/**
Created by Erdi Özbek
-15.12.2024-
-01:22-
 **/

class ExoplayerAnalyticsListener(private val radioAnalyticsOperationRepository: RadioAnalyticsOperationRepository) :
    AnalyticsListener {

    var lastStationResponseData: RadioStationResponseData = RadioStationResponseData()

    override fun onIsPlayingChanged(eventTime: EventTime, isPlaying: Boolean) {
        super.onIsPlayingChanged(eventTime, isPlaying)
        Timber.d(" onIsPlayingChanged $isPlaying")
        Timber.d(" lastStationResponseData ${lastStationResponseData.toString()}")
        if (!isPlaying) {
            radioAnalyticsOperationRepository.sendRadioPlayingAnalytics(
                RadioPlayingAnalyticsData(
                    radioStationUuid = lastStationResponseData.stationuuid,
                    radioName = lastStationResponseData.radioName,
                    playingTimeMilis = eventTime.currentPlaybackPositionMs
                )
            )
        }

        Timber.d(" onIsPlayingChanged ${eventTime.currentPlaybackPositionMs}")
    }
}