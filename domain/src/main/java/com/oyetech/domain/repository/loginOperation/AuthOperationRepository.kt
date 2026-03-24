package com.oyetech.domain.repository.loginOperation

import com.oyetech.models.firebaseModels.userModel.UserDataProperty
import kotlinx.coroutines.flow.MutableStateFlow

interface AuthOperationRepository {
    val userDataStateFlow: MutableStateFlow<UserDataProperty?>

    suspend fun loginWithGoogleAndSyncUser(): Result<UserDataProperty>

    // ViewModel passes only user-facing fields; token/userId are resolved internally by the repository
    suspend fun updateUserProfile(
        username: String,
        age: String,
        gender: String,
    ): Result<UserDataProperty>

    suspend fun deleteAccount(): Result<Unit>
}

