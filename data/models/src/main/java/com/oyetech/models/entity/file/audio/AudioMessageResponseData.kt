package com.oyetech.models.entity.file.audio

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.oyetech.models.BR
import com.oyetech.models.utils.moshi.serialize
import com.oyetech.models.utils.states.FileOperationState
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Suppress("ConstructorParameterNaming")
@Keep
@Parcelize
data class AudioMessageResponseData(
    @Json(name = "audioId") var audioId: String = "",
    @Json(name = "audioUrl") var audioUrl: String = "",
    @Json(name = "duration") var totalDuration: Long = 0L,

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
            notifyPropertyChanged(BR.progressStatus)
        }
}

@Suppress("ConstructorParameterNaming")
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AudioMessageResponseSubData(
    @Json(name = "audioId") var audioId: String = "",
    @Json(name = "audioUrl") var audioUrl: String = "",
    @Json(name = "duration") var totalDuration: Long = 0L,
    var audioFilePath: String = "",

    ) : Parcelable

@Suppress("ConstructorParameterNaming")
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class AudioMessageResponseSocketData(
    @Json(name = "audioId") var audioId: String = "",
    @Json(name = "audioUrl") var audioUrl: String = "",
    @Json(name = "duration") var totalDuration: Long = 0L,
) : Parcelable

fun AudioMessageResponseSubData.mapToNormalize(): AudioMessageResponseData {
    var audioMessageResponseData = AudioMessageResponseData(
        audioId = this.audioId,
        audioUrl = this.audioUrl,
        totalDuration = this.totalDuration,
        audioFilePath = this.audioFilePath
    )
    return audioMessageResponseData
}

fun AudioMessageResponseData.serializeContent(): String {
    var audioMessageResponseData = AudioMessageResponseSubData(
        audioId = this.audioId,
        audioUrl = this.audioUrl,
        totalDuration = this.totalDuration,
        audioFilePath = this.audioFilePath
    )
    return audioMessageResponseData.serialize()
}
