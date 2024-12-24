package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.userModel.FirebaseUserModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-24.12.2024-
-01:03-
 **/

interface FirebaseUserRepository {
    val userDataStateFlow: MutableStateFlow<FirebaseUserModel?>
    fun createProfile(user: FirebaseUserModel)
    fun checkUsername(username: String): Boolean
    fun deleteUser(uid: String)
    fun updateLastLogin(uid: String)
}