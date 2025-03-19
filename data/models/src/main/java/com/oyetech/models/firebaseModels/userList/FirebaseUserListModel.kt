package com.oyetech.models.firebaseModels.userList

import androidx.annotation.Keep
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import java.util.Date

/**
Created by Erdi Özbek
-26.02.2025-
-20:15-
 **/

@Keep
data class FirebaseUserListModel(
    var userId: String = "",
    var username: String = "",
    var age: String = "",
    var gender: String = "",

    @ServerTimestamp
    var lastTriggeredTime: Date? = null,
)

fun FirebaseUserProfileModel.toMapFirebaseUserListModel(): FirebaseUserListModel {
    return FirebaseUserListModel(
        userId = userId,
        username = username,
        age = age,
        gender = gender,
    )

}

fun FirebaseUserProfileModel.toMapListFirebaseUserListModel(): Map<String, Any> {
    return mapOf(
        "lastTriggeredTime" to FieldValue.serverTimestamp(),
        "userId" to userId,
        "username" to username,
        "age" to age,
        "gender" to gender
    )
}