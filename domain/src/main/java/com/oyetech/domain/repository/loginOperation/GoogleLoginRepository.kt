package com.oyetech.domain.repository.loginOperation

import com.oyetech.models.firebaseModels.googleAuth.GoogleAuthResponseData
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-23.06.2024-
-16:42-
 **/

interface GoogleLoginRepository {
    //    val googleAuthStateFlow: MutableStateFlow<GoogleAuthResponseData>
    fun controlUserAlreadySignIn()
    fun signInWithGoogle()
    val googleAuthStateFlow: MutableStateFlow<GoogleAuthResponseData>

    val googleUserStateFlow: MutableStateFlow<GoogleUserResponseData>
    fun signInWithGoogleAnonymous()
    fun autoLoginOperation()
    val userAutoLoginStateFlow: MutableStateFlow<Boolean>
}