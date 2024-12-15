package com.oyetech.composebase.projectRadioFeature.screens.radioListScreen

import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.radioModels.PlayState
import com.oyetech.models.radioProject.radioModels.PlayState.Idle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

sealed class RadioUIEvent {
    object AddAlarm : RadioUIEvent()
    object CreateShortcut : RadioUIEvent()
    object Share : RadioUIEvent()

    data class ToggleFavorite(val state: RadioUIState) : RadioUIEvent()
    data class ExpandItem(val state: RadioUIState) : RadioUIEvent()
    data class TagSelected(val tag: String) : RadioUIEvent()

    data class AddFavorite(val state: RadioUIState) : RadioUIEvent()
}

sealed class RadioPlayerEvent : RadioUIEvent() {
    data class Play(val state: RadioUIState) : RadioPlayerEvent()
    object Next : RadioPlayerEvent()
    object Previous : RadioPlayerEvent()

//    object Pause : RadioPlayerEvent()
//    object Stop : RadioPlayerEvent()
//    object SeekForward : RadioPlayerEvent()
//    object SeekBackward : RadioPlayerEvent()
//    data class SetVolume(val volume: Float) : RadioPlayerEvent()
}

data class RadioUIState(
    val playerState: PlayState = Idle,
    val contentTitle: String = "",
    val isFavorite: Boolean = false,
    val isExpanded: Boolean = false,
    val isSelectedView: Boolean = false,
    val isPlayingView: Boolean = false,

    val id: Int = 0,
    val stationuuid: String = "",
    val radioName: String = "",
    val faviconUrl: String = "",
    val tags: ImmutableList<String> = emptyList<String>().toImmutableList(),
    val stationUrl: String = "",

    )

fun RadioStationResponseData.mapToUiModel(): RadioUIState {
    return RadioUIState(
        stationuuid = this.stationuuid,
        radioName = this.radioName,
        faviconUrl = this.favicon,
        contentTitle = this.radioTitle,
        tags = this.tags.split(",").toImmutableList(),
        stationUrl = this.radioStreamUrl,
//        isFavorite = this.isFavorite,
//        isExpanded = this.isExpanded,
//        isShortcutVisible = this.isShortcutVisible,
    )
}