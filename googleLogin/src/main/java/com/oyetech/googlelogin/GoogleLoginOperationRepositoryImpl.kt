package com.oyetech.googlelogin

import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginOperationRepository
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-23.03.2026-
-21:56-
 **/

class GoogleLoginOperationRepositoryImpl(
    private val activityProviderUseCase: ActivityProviderUseCase,
    private val sharedOperationRepository: SharedOperationRepository,
) : GoogleLoginOperationRepository {
    override val googleUserDataStateFlow: MutableStateFlow<UserProfileProperty?>
        get() = TODO("Not yet implemented")

    override suspend fun signInWithGoogle() {
        //TODO
    }

}