package com.oyetech.domain.repository.loginOperation

import com.oyetech.models.firebaseModels.userModel.UserDataProperty
import kotlinx.coroutines.flow.MutableStateFlow

interface AuthOperationRepository {
    val userDataStateFlow: MutableStateFlow<UserDataProperty?>

    suspend fun loginWithGoogleAndSyncUser(): Result<UserDataProperty>
}

