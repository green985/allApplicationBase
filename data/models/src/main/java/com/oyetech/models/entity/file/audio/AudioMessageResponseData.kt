package com.oyetech.models.entity.file.audio

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.oyetech.models.utils.moshi.serialize
import com.oyetech.models.utils.states.FileOperationState
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Suppress("ConstructorParameterNaming")
@Keep
@Parcelize
data class AudioMessageResponseData(
    @param:Json(name = "audioId") var audioId: String = "",
    @param:Json(name = "audioUrl") var audioUrl: String = "",
    @param:Json(name = "duration") var totalDuration: Long = 0L,

    var audioFilePath: String = "",

    var _progressStatus: Double = if (audioUrl.isNotBlank()) {
        FileOperationState.COMPLETE
    } else {
        FileOperationState.SENDING
    },
) : Parcelable,
    BaseObservable() {

    @Json(ignore = true)
    var progressStatus: Double
        @Bindable get() = _progressStatus
        set(value) {
            if (audioUrl.isNotBlank()) {
                _progressStatus = FileOperationState.COMPLETE
            } else {
                _progressStatus = value
            }
        }
}

@Suppress("ConstructorParameterNaming")
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AudioMessageResponseSubData(
    @param:Json(name = "audioId") var audioId: String = "",
    @param:Json(name = "audioUrl") var audioUrl: String = "",
    @param:Json(name = "duration") var totalDuration: Long = 0L,
    var audioFilePath: String = "",

    ) : Parcelable

@Suppress("ConstructorParameterNaming")
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AudioMessageResponseSocketData(
    @param:Json(name = "audioId") var audioId: String = "",
    @param:Json(name = "audioUrl") var audioUrl: String = "",
    @param:Json(name = "duration") var totalDuration: Long = 0L,
) : Parcelable

fun AudioMessageResponseSubData.mapToNormalize(): AudioMessageResponseData {
    val audioMessageResponseData = AudioMessageResponseData(
        audioId = this.audioId,
        audioUrl = this.audioUrl,
        totalDuration = this.totalDuration,
        audioFilePath = this.audioFilePath
    )
    return audioMessageResponseData
}

fun AudioMessageResponseData.serializeContent(): String {
    val audioMessageResponseData = AudioMessageResponseSubData(
        audioId = this.audioId,
        audioUrl = this.audioUrl,
        totalDuration = this.totalDuration,
        audioFilePath = this.audioFilePath
    )
    return audioMessageResponseData.serialize()
}
