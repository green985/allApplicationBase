package com.oyetech.domain.repository.loginOperation

import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import kotlinx.coroutines.flow.MutableStateFlow

interface AuthOperationRepository {
    val userDataStateFlow: MutableStateFlow<UserProfileProperty?>

    suspend fun loginWithGoogleAndSyncUser(): Result<UserProfileProperty>

    suspend fun syncUserFromSavedSession(): Result<UserProfileProperty>

    suspend fun logoutAndClearSession(): Result<Unit>

    // ViewModel passes only user-facing fields; token/userId are resolved internally by the repository
    suspend fun updateUserProfile(
        username: String,
        age: String,
        gender: String,
    ): Result<UserProfileProperty>

    suspend fun deleteAccount(): Result<Unit>

    suspend fun getToken(): String

    suspend fun getUserId(): String
}
