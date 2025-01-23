package com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.vm

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.mapToUiModel
import com.oyetech.composebase.projectRadioFeature.viewModelSlice.IRadioPlayerViewModelSlice
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.radioModels.RadioViewStateNew
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-24.11.2024-
-17:49-
 **/

class RadioPlayerVM(
    private val dispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private val radioOperationUseCase: RadioOperationUseCase,
    private val radioDataOperationUseCase: RadioDataOperationUseCase,
    private val radioPlayerViewModelSlice: IRadioPlayerViewModelSlice,
) : BaseViewModel(dispatchers) {

    val radioUIState =
        MutableStateFlow(
            radioDataOperationUseCase.getLastRadioData()?.mapToUiModel() ?: RadioUIState()
        )

    init {
        getPlayerFavoriteOperationFlow()
        getPlayerRadioFlow()
    }

    fun getPlayerRadioFlow() {
        viewModelScope.launch(dispatchers.io) {
            radioOperationUseCase.radioViewStateNewMutableStateFlow.asStateFlow().onEach {
                updateState(it)
            }.collect()
        }
    }

    private fun controlAndSetIfFavorite() {
        viewModelScope.launch(getDispatcherIo()) {

            val isFav = radioUIState.value.stationuuid.let { it1 ->
                radioDataOperationUseCase.getRadioFavWithStationUuid(it1)
            } != null
            radioUIState.updateState { copy(isFavorite = isFav) }
        }
    }

    private fun getPlayerFavoriteOperationFlow() {
        viewModelScope.launch(dispatchers.io) {
            radioDataOperationUseCase.getRadioFavListFlow().onEach {
                val isFav = radioUIState.value.stationuuid?.let { it1 ->
                    radioDataOperationUseCase.getRadioFavWithStationUuid(it1)
                } != null
                radioUIState.updateState { copy(isFavorite = isFav) }
            }.collect()
        }
    }

    fun updateState(radioViewStateNew: RadioViewStateNew) {
        val status = radioViewStateNew.status
        var radioUiModel = radioViewStateNew.data?.mapToUiModel()

        if (radioUiModel == null) {
            Timber.d("Neden null bilmiyorum aslanim")
            return
        }

        radioUiModel = radioUiModel.copy(playerState = status)

        radioUIState.updateState { radioUiModel }
        controlAndSetIfFavorite()
    }

    fun handleRadioEvent(
        it: RadioUIEvent,
        complexItemViewState: MutableStateFlow<ComplexItemListState<RadioUIState>>?,
    ) {
        radioPlayerViewModelSlice.handleRadioEvent(it, complexItemViewState)
    }

}