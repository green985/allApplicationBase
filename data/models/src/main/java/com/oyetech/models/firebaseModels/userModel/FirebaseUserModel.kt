package com.oyetech.models.firebaseModels.userModel

import com.oyetech.models.utils.helper.TimeFunctions

/**
Created by Erdi Özbek
-19.06.2024-
-23:22-
 **/

data class FirebaseUserModel(
    val name: String,
    val uid: String,
    val lastLoginDateString: String = TimeFunctions.getFullDateFromLongMilis(System.currentTimeMillis()),
)