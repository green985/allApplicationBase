package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-24.12.2024-
-01:03-
 **/

interface FirebaseUserRepository {
    val userDataStateFlow: MutableStateFlow<FirebaseUserProfileModel?>

    //    fun createProfile(user: FirebaseUserProfileModel)
//    fun checkUsername(username: String): Boolean
    fun deleteUser(uid: String)

    //    fun updateLastLogin(uid: String)
    fun getUserProfileWithUid(firebaseProfileUserModel: FirebaseUserProfileModel)

    suspend fun updateUserName(username: String)
    fun getUsername(): String
    fun getUserId(): String
    fun isMyContent(contentUsername: String): Boolean
    fun getUserProfileForAutoLogin(
        firebaseProfileUserModel: FirebaseUserProfileModel,
        afterAction: (Boolean) -> Unit,
    )

    suspend fun updateUserName(userData: FirebaseUserProfileModel)
    fun getUserProfileModel(): MutableStateFlow<FirebaseUserProfileModel?>
}