package com.oyetech.models.firebaseModels.userModel

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude

/**
Created by Erdi Özbek
-19.06.2024-
-23:22-
 **/

@Keep
data class UserProfileProperty(
    val isAnonymous: Boolean = false,
    val notificationToken: String = "",
    val firebaseToken: String = "",
    val accessToken: String = "",

    val lastSignInTimestamp: String = "",
    val lastSignInTimestampTmp: Long? = null,
    val creationTimestamp: String = "",

    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val gender: String = "",
    val age: String = "",
    val biography: String = "",
) {
    fun isProfileComplete(): Boolean {
        return username.isNotBlank() &&
                userId.isNotBlank() &&
                gender.isNotBlank()
    }
}

@Keep
data class FirebaseUserProfileModel(
    @get:Exclude
    val errorException: Exception? = null, // Added field

    val isAnonymous: Boolean = false,
    val notificationToken: String = "",

    val lastSignInTimestamp: Long? = null,
    val creationTimestamp: Long = System.currentTimeMillis(),

    val userId: String = "",
    val username: String = "",
    val gender: String = "",
    val age: String = "",
    val biography: String = "",
) {
    fun isUserDeleted(): Boolean {
        return userId != "" && username == ""
    }

    fun isProfileComplete(): Boolean {
        return username.isNotBlank() &&
                userId.isNotBlank() &&
                gender.isNotBlank()
    }
}

fun UserProfileProperty.toFirebaseUserProfileModel(): FirebaseUserProfileModel {
    return FirebaseUserProfileModel(
        isAnonymous = this.isAnonymous,
        notificationToken = this.notificationToken,
//        lastSignInTimestamp = this.lastSignInTimestampTmp,
//        creationTimestamp = this.creationTimestamp,
        userId = this.userId,
        username = this.username,
        gender = this.gender,
        age = this.age,
        biography = this.biography,
    )
}
