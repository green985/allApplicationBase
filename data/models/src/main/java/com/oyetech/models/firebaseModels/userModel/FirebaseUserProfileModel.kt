package com.oyetech.models.firebaseModels.userModel

/**
Created by Erdi Özbek
-19.06.2024-
-23:22-
 **/

data class FirebaseUserProfileModel(
    val isAnonymous: Boolean = false,
    val username: String = "",
    val uid: String = "",

    @Transient
    val errorException: Exception? = null, // Added field

    val lastSignInTimestamp: Long? = null,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val createdAtFieldValue: Any? = null,
)

