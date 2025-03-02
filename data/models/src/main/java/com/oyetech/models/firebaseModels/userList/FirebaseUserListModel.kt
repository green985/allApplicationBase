package com.oyetech.models.firebaseModels.userList

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
Created by Erdi Özbek
-26.02.2025-
-20:15-
 **/

@Keep
data class FirebaseUserListModel(
    var documentId: String = "",
    var userId: String = "",
    var username: String = "",

    @ServerTimestamp
    var joinedAt: Date? = null,
)