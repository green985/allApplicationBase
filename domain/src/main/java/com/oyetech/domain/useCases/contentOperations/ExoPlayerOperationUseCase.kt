package com.oyetech.domain.useCases.contentOperations

import com.oyetech.domain.repository.contentOperation.ExoPlayerOperationRepository
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData

/**
Created by Erdi Özbek
-15.11.2022-
-17:34-
 **/

class ExoPlayerOperationUseCase(
    private var exoPlayerOperationRepository: ExoPlayerOperationRepository,
//    private var analyticsOperationUseCase: AnalyticsOperationUseCase
) {

    fun stopPlayer() {
        exoPlayerOperationRepository.stop()
    }

    fun pausePlayer() {
        exoPlayerOperationRepository.pause()
    }

    fun resumePlayer() {
        exoPlayerOperationRepository.resumePlayer()
    }

    fun setVolume(volume: Float) {

        exoPlayerOperationRepository.setVolume(volume)
    }

    fun startPlayer(radioModel: RadioStationResponseData?) {
//        analyticsOperationUseCase.logEventWithKey(AnalyticsKeys.radio_play_click)
        exoPlayerOperationRepository.startPlayer(radioModel)
    }

    fun startPlayerForAlarm(radioModel: RadioStationResponseData?) {
        exoPlayerOperationRepository.startPlayerForAlarm(radioModel)
    }

}

var examplePodcastUrl =
    "https://media.simplecastcdn.com/695767b0-cd40-4e6c-ac8c-ac6bc0df77ee/episodes/f15e24b4-1597-428b-8ba0-d602ab91dc44/audio/33a8ac9a-b88c-4880-8fd2-b93045e46d1b/128/default_tc.mp3?cid[]=996b713d-0f3a-4d9d-84fa-b6abe7ca0e44"