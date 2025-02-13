package com.oyetech.models.firebaseModels.contentOperationModel

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Keep
data class LikeOperationModel(
    val contentId: String = "",
    val username: String = "",
    val likeId: String = "",
    val isLiked: Boolean = false,

    @ServerTimestamp
    val createdAt: Date? = null,
)
