package com.oyetech.models.firebaseModels.contentOperationModel

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import com.squareup.moshi.JsonClass
import java.util.Date

@Entity(
    tableName = "contentLike"
)
@JsonClass(generateAdapter = true)
@Keep
data class LikeOperationModel(
    @PrimaryKey
    var contentId: String = "",
    var username: String = "",
    var likeId: String = "",
    var like: Boolean = false,

    @ServerTimestamp
    @Ignore
    var createdAt: Date? = null,
)
