package com.oyetech.models.firebaseModels.contentOperationModel

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "contentLike"
)
@Keep
data class LikeOperationModel(
    @PrimaryKey
    var contentId: String = "",
    var username: String = "",
    var likeId: String = "",
    var like: Boolean = false,

)
