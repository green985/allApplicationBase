@file:OptIn(DelicateCoroutinesApi::class)

package com.oyetech.domain.useCases.contentOperations

import com.oyetech.domain.repository.contentOperation.RadioOperationRepository
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.radioModels.PauseReason
import com.oyetech.models.radioProject.radioModels.PauseReason.NONE
import com.oyetech.models.radioProject.radioModels.PlayState
import com.oyetech.models.radioProject.radioModels.PlayState.Idle
import com.oyetech.models.radioProject.radioModels.PlayState.Paused
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import com.oyetech.models.radioProject.radioModels.PlayState.PrePlaying
import com.oyetech.models.radioProject.radioModels.RadioViewStateNew
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import timber.log.Timber

/**
Created by Erdi Özbek
-15.11.2022-
-17:33-
 **/

class RadioOperationUseCase(
    private var radioOperationRepository: RadioOperationRepository,
) {
    var lastStation: RadioStationResponseData? = null

    var radioViewStateNewMutableStateFlow =
        MutableStateFlow(RadioViewStateNew.idle(data = lastStation))

    var radioErrorStateMutableStateFlow = MutableStateFlow("")

    val radioPauseReasonMutableStateFlow = MutableStateFlow(NONE)

    init {
        Timber.d("radio operation initttt")
    }

    private fun getRadioOperationServiceCollector(): FlowCollector<RadioViewStateNew> {
        val collector = object : FlowCollector<RadioViewStateNew> {
            override suspend fun emit(value: RadioViewStateNew) {
                if (!currentCoroutineContext().isActive) {
                    Timber.d("RadioOperationUseCase jobs cannecled")
                    return
                }
                val status = value.status
                when (status) {
                    Idle -> {
                    }

                    PrePlaying -> {
                        radioPauseReasonMutableStateFlow.value = NONE
                    }

                    Playing -> {
                        radioPauseReasonMutableStateFlow.value = NONE
                    }

                    Paused -> {
                    }
                }
            }
        }

        return collector
    }

    @OptIn(InternalCoroutinesApi::class)
    suspend fun observeAndSetPauseReasonWithRadioStateChange() {
        radioViewStateNewMutableStateFlow.collect(getRadioOperationServiceCollector())
    }

    fun getPlayerPauseReason(): PauseReason {
        return radioPauseReasonMutableStateFlow.value
    }

    fun startLastRadio() {
        Timber.d("startLastRadio")
        radioOperationRepository.resumePlayer()
    }

    fun isPlaying(): Boolean {
        return radioViewStateNewMutableStateFlow.value.status == Playing
    }

    fun startPlayer(radioModel: RadioStationResponseData? = null) {
        Timber.d(" startPlayer radioModel == $radioModel")
        if (lastStation == radioModel) {
            if (isPlaying()) {
                // do nothing...
                Timber.d("already playing...")
                return
            }
        }

        lastStation = radioModel

        setRadioViewStateData(PrePlaying)
        radioOperationRepository.startPlayer(lastStation)
    }

    fun stopPlayer() {
        setRadioViewStateData(Idle)
        radioOperationRepository.stopPlayer()
    }

    fun pausePlayer(pauseReason: PauseReason) {
        radioOperationRepository.pausePlayer()
        setRadioViewStateData(Paused)
        setPauseReason(pauseReason)
    }

    fun setRadioViewStateData(state: PlayState, errorText: String = "") {
        val radioDataModel = RadioViewStateNew(state, lastStation, errorText)

        radioViewStateNewMutableStateFlow.value = radioDataModel
    }

    fun setRadioTitleData(title: String?) {
        if (title.isNullOrBlank()) {
            lastStation?.radioTitle = ""
        } else {
            lastStation?.radioTitle = title
        }
        val state = getPlayerState()

        radioViewStateNewMutableStateFlow.value =
            RadioViewStateNew(status = state, data = lastStation)
    }

    fun setRadioVsiewStateData(state: PlayState, errorText: String = "") {
        val radioDataModel = RadioViewStateNew(state, lastStation, errorText)

        radioViewStateNewMutableStateFlow.value = radioDataModel
    }

    fun setPauseReason(pauseReason: PauseReason) {
        radioPauseReasonMutableStateFlow.value = pauseReason
    }

    fun resumePlayer() {
        radioOperationRepository.resumePlayer()
    }

    fun setVolume(fullVolume: Float) {
        radioOperationRepository.setVolume(fullVolume)
    }

    fun getPlayerState(): PlayState {
        return radioViewStateNewMutableStateFlow.value.status
    }

    fun nextStationRadioChannel() {
        // no-op: local station list navigation removed
        Timber.d("nextStationRadioChannel: local DB removed, no-op")
    }

    fun previousRadioChannel() {
        // no-op: local station list navigation removed
        Timber.d("previousRadioChannel: local DB removed, no-op")
    }

    fun changeRadioErrorState(errorString: String) {
        radioErrorStateMutableStateFlow.value = errorString
    }
}
