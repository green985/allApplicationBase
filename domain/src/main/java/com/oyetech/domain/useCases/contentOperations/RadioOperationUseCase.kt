package com.oyetech.domain.useCases.contentOperations

import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-15.11.2022-
-17:33-
 **/

class RadioOperationUseCase(
    private var appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private var radioOperationRepository: RadioOperationRepository,
    private var radioDataOperationUseCase: RadioDataOperationUseCase,
) {
    var lastStation = radioDataOperationUseCase.lastRadioDataa

    var radioViewStateNewMutableStateFlow =
        MutableStateFlow(RadioViewStateNew.idle(data = lastStation))

    var radioErrorStateMutableStateFlow = MutableStateFlow("")

    val radioPauseReasonMutableStateFlow = MutableStateFlow(PauseReason.NONE)

    init {
        Timber.d("radio operation initttt")
    }

    private fun getRadioOperationServiceCollector(): FlowCollector<RadioViewStateNew> {
        var collector = object : FlowCollector<RadioViewStateNew> {
            override suspend fun emit(value: RadioViewStateNew) {
                if (!currentCoroutineContext().isActive) {
                    Timber.d("RadioOperationUseCase jobs cannecled")
                    return
                }
                var status = value.status
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

    fun startPlayer(
        radioModel: RadioStationResponseData? = null,
        radioListFromLiveData: List<RadioStationResponseData>? = null,
    ) {
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


        if (radioModel != null) {
            prepareRadioDataOperation(radioModel, radioListFromLiveData)
        }
    }

    fun startPlayerForAlarm(radioModel: RadioStationResponseData) {
        lastStation = radioModel
        setRadioViewStateData(PrePlaying)
        radioOperationRepository.startPlayerForAlarm(radioModel)

        if (radioModel != null) {
            prepareRadioDataOperation(radioModel, null)
        }
    }

    fun prepareRadioDataOperation(
        radioModel: RadioStationResponseData,
        radioListFromLiveData: List<RadioStationResponseData>? = null,
    ) {
        GlobalScope.launch(appDispatchers.io) {
            radioDataOperationUseCase.prepareRadioDataOperation(
                radioModel,
                radioListFromLiveData
            )
        }
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
        var radioDataModel = RadioViewStateNew(state, lastStation, errorText)

        radioViewStateNewMutableStateFlow.value = radioDataModel
    }

    fun setRadioTitleData(title: String?) {
        if (title.isNullOrBlank()) {
            lastStation?.radioTitle = ""
        } else {
            lastStation?.radioTitle = title
        }
        var state = getPlayerState()

        radioViewStateNewMutableStateFlow.value =
            RadioViewStateNew(status = state, data = lastStation)
    }

    fun setRadioVsiewStateData(state: PlayState, errorText: String = "") {
        var radioDataModel = RadioViewStateNew(state, lastStation, errorText)

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

    fun nextStationRadioChannel() {
        GlobalScope.launch(appDispatchers.io) {
            var radioDataModel = radioDataOperationUseCase.getNextRadioStation()
            startPlayer(radioDataModel)
        }
    }

    fun previousRadioChannel() {
        GlobalScope.launch(appDispatchers.io) {
            var radioDataModel = radioDataOperationUseCase.getPreviousRadioStation()
            startPlayer(radioDataModel)
        }
    }

    fun getPlayerState(): PlayState {
        return radioViewStateNewMutableStateFlow.value.status
    }

    fun changeRadioErrorState(errorString: String) {
        radioErrorStateMutableStateFlow.value = errorString
    }

    suspend fun findStation(stationUuid: String): RadioStationResponseData? {
        return radioDataOperationUseCase.getRadioWithStationUuid(stationUuid)
    }


}