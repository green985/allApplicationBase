package com.oyetech.models.radioProject.radioModels

import androidx.annotation.Keep
import com.oyetech.models.entity.contentProperties.ContentPlayerDetailsModel
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.radioModels.PlayState.Idle
import com.oyetech.models.radioProject.radioModels.PlayState.Paused
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import com.oyetech.models.radioProject.radioModels.PlayState.PrePlaying

/*
data class RadioViewState<out T>(
    val status: RadioStatus,
    val data: T,
    val message: String? = null
) {

    companion object {
        fun <T> playing(data: T): RadioViewState<T> {
            return RadioViewState(RadioStatus.PLAYING, data, null)
        }

        fun <T> stop(data: T): RadioViewState<T> {
            return RadioViewState(RadioStatus.PAUSE, data, null)
        }

        fun <T> error(data: T, msg: String): RadioViewState<T> {
            return RadioViewState(RadioStatus.ERROR, data, msg)
        }

        fun <T> loading(data: T): RadioViewState<T> {
            return RadioViewState(RadioStatus.LOADING, data, null)
        }
    }
}


data class RadioViewStateNew(
    val status: RadioStatus,
    val data: RadioDataModel?,
    val message: String? = null
) {

    companion object {
        fun playing(data: RadioDataModel): RadioViewStateNew {
            return RadioViewStateNew(RadioStatus.PLAYING, data, null)
        }

        fun stop(data: RadioDataModel): RadioViewStateNew {
            return RadioViewStateNew(RadioStatus.PAUSE, data, null)
        }

        fun error(data: RadioDataModel, msg: String): RadioViewStateNew {
            return RadioViewStateNew(RadioStatus.ERROR, data, msg)
        }

        fun loading(data: RadioDataModel? = null): RadioViewStateNew {
            return RadioViewStateNew(RadioStatus.LOADING, data, null)
        }
    }
}

 */
@Keep
data class RadioViewStateNew(
    val status: PlayState,
    val data: RadioStationResponseData?,
    val message: String? = null,
) {

    companion object {
        fun playing(data: RadioStationResponseData): RadioViewStateNew {
            return RadioViewStateNew(Playing, data, null)
        }

        fun paused(data: RadioStationResponseData): RadioViewStateNew {
            return RadioViewStateNew(Paused, data, null)
        }

        fun idle(data: RadioStationResponseData? = null, msg: String = ""): RadioViewStateNew {
            return RadioViewStateNew(Idle, data, msg)
        }

        fun prePlaying(data: RadioStationResponseData? = null): RadioViewStateNew {
            return RadioViewStateNew(PrePlaying, data, null)
        }
    }
}

@Keep
data class ContentStateView(
    val status: PlayState,
    val data: ContentPlayerDetailsModel?,
    val message: String? = null,
) {

    companion object {
        fun playing(data: ContentPlayerDetailsModel): ContentStateView {
            return ContentStateView(Playing, data, null)
        }

        fun paused(data: ContentPlayerDetailsModel): ContentStateView {
            return ContentStateView(Paused, data, null)
        }

        fun idle(data: ContentPlayerDetailsModel? = null, msg: String = ""): ContentStateView {
            return ContentStateView(Idle, data, msg)
        }

        fun prePlaying(data: ContentPlayerDetailsModel? = null): ContentStateView {
            return ContentStateView(PrePlaying, data, null)
        }
    }
}