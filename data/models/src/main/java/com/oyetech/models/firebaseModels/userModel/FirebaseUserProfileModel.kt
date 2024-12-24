package com.oyetech.models.firebaseModels.userModel

import com.google.firebase.firestore.Exclude

/**
Created by Erdi Özbek
-19.06.2024-
-23:22-
 **/

data class FirebaseUserProfileModel(
    @get:Exclude
    val errorException: Exception? = null, // Added field

    @get:Exclude
    val isUserDeleted: Boolean = false,

    val isAnonymous: Boolean = false,
    val username: String = "",
    val uid: String = "",

    val lastSignInTimestamp: Long? = null,
    val creationTimestamp: Long = System.currentTimeMillis(),
)

