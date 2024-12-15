package com.oyetech.models.postBody.messages

import androidx.annotation.Keep
import com.oyetech.models.entity.file.audio.AudioMessageResponseData
import com.oyetech.models.entity.file.audio.AudioMessageResponseSocketData
import com.oyetech.models.entity.file.imageFile.ImageMessageResponseData
import com.oyetech.models.entity.file.imageFile.ImageMessageResponseSocketSendData
import com.oyetech.models.sealeds.messagess.MessageTypeUtil
import com.oyetech.models.utils.moshi.deserialize
import com.oyetech.models.utils.moshi.serialize
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import timber.log.Timber

/**
Created by Erdi Özbek
-7.03.2022-
-22:15-
 **/
@Keep
@JsonClass(generateAdapter = true)
data class MessageRequestBody(
    // looks like a guid...
    @Json(name = "tempId") var tempId: String = "",
    @Json(name = "id") var id: Long = 0,
    @Json(name = "conversationId") var conversationId: Long = 0,
    @Json(name = "toUserId") var toUserId: Long = 0,
    @Json(name = "content") var content: String = "",
    @Json(name = "contentType") var contentType: String = "",
    @Json(name = "date") var date: String = "",

    @Transient var imageUploadResponseData: ImageMessageResponseData? = null,
    @Transient var audioUploadResponseData: AudioMessageResponseData? = null,
    @Transient var toUserName: String = "",
    @Transient var profileImageUrl: String = ""
)

fun MessageRequestBody.serializeWithRemoveUnwantedObject(): String {
    var newContentObject: Any? = Any()
    if (contentType == MessageTypeUtil.Image()) {
        newContentObject = content.deserialize<ImageMessageResponseSocketSendData>()
    } else if (contentType == MessageTypeUtil.Audio()) {
        newContentObject = content.deserialize<AudioMessageResponseSocketData>()
        Timber.d("audiooooo")
    }

    var newContentObjectString = newContentObject.serialize()
    if (newContentObjectString == "{}" || newContentObjectString.isNullOrBlank()) {
        Timber.d("serilize problem...")
        newContentObjectString = content
    }
    content = newContentObjectString
    return this.serialize()
}
