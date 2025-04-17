package com.oyetech.models.firebaseModels.firebaseToken

import androidx.annotation.Keep
import androidx.room.Ignore
import com.google.firebase.firestore.ServerTimestamp
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
@Keep
data class FirebaseTokenOperationModel(
    var notificationToken: String = "",
    var userId: String = "",

    @ServerTimestamp
    @Ignore
    var createdAt: Date? = null,
)
