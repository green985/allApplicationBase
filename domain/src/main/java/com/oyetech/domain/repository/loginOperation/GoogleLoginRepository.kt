package com.oyetech.domain.repository.loginOperation

import com.oyetech.models.firebaseModels.googleAuth.GoogleAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-23.06.2024-
-16:42-
 **/

interface GoogleLoginRepository {
    val googleAuthStateFlow: MutableStateFlow<GoogleAuthResponseData>
    fun controlUserAlreadySignIn()
    fun signInWithGoogle()
}