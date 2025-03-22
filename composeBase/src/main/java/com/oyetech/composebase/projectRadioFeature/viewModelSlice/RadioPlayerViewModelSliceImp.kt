package com.oyetech.composebase.projectRadioFeature.viewModelSlice

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.ComplexItemListState
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioPlayerEvent.Next
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioPlayerEvent.Play
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioPlayerEvent.Previous
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.AddAlarm
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.AddFavorite
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.CreateShortcut
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.ExpandItem
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.Share
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.TagSelected
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.ToggleFavorite
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

interface IRadioPlayerViewModelSlice {
    context(BaseViewModel)
    fun handleRadioEvent(
        it: RadioUIEvent,
        complexItemViewState: MutableStateFlow<ComplexItemListState<RadioUIState>>?,
    )
}

/**
Created by Erdi Özbek
-24.11.2024-
-21:21-
 **/

class RadioPlayerViewModelSliceImp(
    private val radioOperationUseCase: RadioOperationUseCase,
    private val radioDataOperationUseCase: RadioDataOperationUseCase,
) : IRadioPlayerViewModelSlice {

    context(BaseViewModel)
    override fun handleRadioEvent(
        it: RadioUIEvent,
        complexItemViewState: MutableStateFlow<ComplexItemListState<RadioUIState>>?,
    ) {
        when (it) {

            AddAlarm -> TODO()
            CreateShortcut -> TODO()
            Share -> TODO()
            is TagSelected -> {
                // make analytics operation later...
                handleTagEvent(it.tag)
            }

            is ToggleFavorite -> {
                handleFavEvent(it.state)
            }

            is ExpandItem -> {
                handleExpandEvent(it.state, complexItemViewState)
            }

            is Play -> {
                playPauseSelectedRadio(it.state)
            }

            is Next -> {
                radioOperationUseCase.nextStationRadioChannel()
            }

            is Previous -> {
                radioOperationUseCase.previousRadioChannel()
            }

            is AddFavorite -> {
                favOperation(it.state)
            }
        }
    }

    private fun handleTagEvent(tag: String) {
        // make analytics operation later...
    }

    private fun playPauseSelectedRadio(
        baseViewModel: BaseViewModel,
        selectedItem: RadioUIState,
    ) {
        with(baseViewModel) {
            if (selectedItem.playerState == Playing) {
                radioOperationUseCase.stopPlayer()
                return
            }


            viewModelScope.launch(getDispatcherIo()) {
                val selectedRadioStation =
                    radioOperationUseCase.findStation(selectedItem.stationuuid)
                radioOperationUseCase.startPlayer(selectedRadioStation)
            }
        }
    }

    context(BaseViewModel)
    private fun handleFavEvent(
        state: RadioUIState,
    ) {
        favOperation(state)
    }

    context(BaseViewModel)
    fun favOperation(itemData: RadioUIState?) {

        val radioStationId = if (itemData == null) {
            radioDataOperationUseCase.getLastRadioData()?.stationuuid
        } else {
            itemData.stationuuid
        }


        viewModelScope.launch(getDispatcherIo()) {
            val radioModel =
                radioStationId?.let { radioOperationUseCase.findStation(it) } ?: return@launch

            val favList = radioDataOperationUseCase.getRadioFavList()

            val model = favList.find {
                it.stationUuid == radioStationId
            }

            if (model != null) {
                removeFavList(radioModel)
            } else {
                putFavList(radioModel)
            }
        }

        return
    }

    context(BaseViewModel)
    fun putFavList(itemData: RadioStationResponseData) {
        GlobalScope.launch(getDispatcherIo()) {
            radioDataOperationUseCase.addToFavList(itemData)
        }
    }

    context(BaseViewModel)
    fun removeFavList(itemData: RadioStationResponseData) {
        GlobalScope.launch(getDispatcherIo()) {
            radioDataOperationUseCase.removeToFavList(itemData)
        }
    }

    private fun handleExpandEvent(
        state: RadioUIState,
        complexItemViewState: MutableStateFlow<ComplexItemListState<RadioUIState>>?,
    ) {
        complexItemViewState?.updateState {
            copy(
                items = items.map {
                    if (it.stationuuid == state.stationuuid) {
                        it.copy(isExpanded = !it.isExpanded)
                    } else {
                        it
                    }
                }.toImmutableList()
            )
        }
    }
}