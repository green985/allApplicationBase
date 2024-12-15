package com.oyetech.models.entity.file.imageFile

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

/**
Created by Erdi Özbek
-7.04.2022-
-01:42-
 **/

@Keep
@Parcelize
@Suppress("ConstructorParameterNaming")
data class ImageMessageResponseData(
    @Json(name = "aspectRatio") var aspectRatio: Double = 0.0,
    @Json(name = "aspectRatioString") var aspectRatioString: String = "",
    @Json(name = "blurredImageBase64") var blurredImageBase64: String = "",
    @Json(name = "largeImageId") var largeImageId: String = "",
    @Json(name = "largeImageSize")
    var largeImageSize: ImagePropertyResponseData = ImagePropertyResponseData(),
    @Json(name = "largeImageUrl") var largeImageUrl: String = "",
    @Json(name = "thumbnailImageId") var thumbnailImageId: String = "",
    @Json(name = "thumbnailImageSize")
    var thumbnailImageSize: ImagePropertyResponseData = ImagePropertyResponseData(),
    @Json(name = "thumbnailImageUrl") var thumbnailImageUrl: String = "",

    var imageFilePath: String = "",

    @Transient var _progressStatus: Double = if (largeImageUrl.isNotBlank()) {
        FileOperationState.COMPLETE
    } else {
        0.0
    },
) : Parcelable,
    BaseObservable() {

    @Json(ignore = true)
    var progressStatus: Double
        @Bindable get() = _progressStatus
        set(value) {
            if (largeImageUrl.isNotBlank()) {
                _progressStatus = FileOperationState.COMPLETE
            } else {
                _progressStatus = value
            }
            notifyPropertyChanged(BR.progressStatus)
        }
}

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
@Suppress("ConstructorParameterNaming")
data class ImageMessageResponseSubData(
    @Json(name = "aspectRatio") var aspectRatio: Double = 0.0,
    @Json(name = "aspectRatioString") var aspectRatioString: String = "",
    @Json(name = "blurredImageBase64") var blurredImageBase64: String = "",
    @Json(name = "largeImageId") var largeImageId: String = "",
    @Json(name = "largeImageSize")
    var largeImageSize: ImagePropertyResponseData = ImagePropertyResponseData(),
    @Json(name = "largeImageUrl") var largeImageUrl: String = "",
    @Json(name = "thumbnailImageId") var thumbnailImageId: String = "",
    @Json(name = "thumbnailImageSize")
    var thumbnailImageSize: ImagePropertyResponseData = ImagePropertyResponseData(),
    @Json(name = "thumbnailImageUrl") var thumbnailImageUrl: String = "",
    var imageFilePath: String = "",

    ) : Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
@Suppress("ConstructorParameterNaming")
data class ImageMessageResponseSocketSendData(
    @Json(name = "aspectRatio") var aspectRatio: Double = 0.0,
    @Json(name = "aspectRatioString") var aspectRatioString: String = "",
    @Json(name = "blurredImageBase64") var blurredImageBase64: String = "",
    @Json(name = "largeImageId") var largeImageId: String = "",
    @Json(name = "largeImageSize")
    var largeImageSize: ImagePropertyResponseData = ImagePropertyResponseData(),
    @Json(name = "largeImageUrl") var largeImageUrl: String = "",
    @Json(name = "thumbnailImageId") var thumbnailImageId: String = "",
    @Json(name = "thumbnailImageSize")
    var thumbnailImageSize: ImagePropertyResponseData = ImagePropertyResponseData(),
    @Json(name = "thumbnailImageUrl") var thumbnailImageUrl: String = "",
) : Parcelable

fun ImageMessageResponseSubData.mapToNormalize(): ImageMessageResponseData {
    var imageMessageResponseData = ImageMessageResponseData(
        aspectRatio = this.aspectRatio,
        aspectRatioString = this.aspectRatioString,
        blurredImageBase64 = this.blurredImageBase64,
        largeImageId = this.largeImageId,
        largeImageSize = ImagePropertyResponseData(
            height = this.largeImageSize.height,
            width = this.largeImageSize.width
        ),
        largeImageUrl = this.largeImageUrl,
        thumbnailImageId = this.thumbnailImageId,
        thumbnailImageSize = ImagePropertyResponseData(
            height = this.thumbnailImageSize.height,
            width = this.thumbnailImageSize.width
        ),
        thumbnailImageUrl = this.thumbnailImageUrl,
        imageFilePath = this.imageFilePath
    )
    return imageMessageResponseData
}

fun ImageMessageResponseData.serializeContent(): String {
    var imageMessageResponseData = ImageMessageResponseSubData(
        aspectRatio = this.aspectRatio,
        aspectRatioString = this.aspectRatioString,
        blurredImageBase64 = this.blurredImageBase64,
        largeImageId = this.largeImageId,
        largeImageSize = ImagePropertyResponseData(
            height = this.largeImageSize.height,
            width = this.largeImageSize.width
        ),
        largeImageUrl = this.largeImageUrl,
        thumbnailImageId = this.thumbnailImageId,
        thumbnailImageSize = ImagePropertyResponseData(
            height = this.thumbnailImageSize.height,
            width = this.thumbnailImageSize.width
        ),
        thumbnailImageUrl = this.thumbnailImageUrl,
        imageFilePath = this.imageFilePath
    )
    return imageMessageResponseData.serialize()
}
