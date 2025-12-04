package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-24.12.2024-
-01:03-
 **/

interface FirebaseUserRepository {
    val userDataStateFlow: MutableStateFlow<FirebaseUserProfileModel>
    val userProfileDataStateFlow: MutableStateFlow<UserProfileProperty>
    fun deleteUser(uid: String)

    fun getUserProfile(firebaseProfileUserModel: FirebaseUserProfileModel)

    fun getUsername(): String
    fun getUserId(): String
    fun isMyContent(contentUsername: String): Boolean
    fun getUserProfileForAutoLogin(
        firebaseProfileUserModel: FirebaseUserProfileModel,
        afterAction: (Boolean) -> Unit,
    )

    suspend fun updateUserProperty(userData: FirebaseUserProfileModel)
    fun getUserProfileModel(): MutableStateFlow<FirebaseUserProfileModel>
    fun updateUserNotificationToken(notificationToken: String)
    fun getUserProfileWithUserId(userId: String): Flow<FirebaseUserProfileModel>
    fun updateUserProfileProperty(updatedUser: UserProfileProperty)
}