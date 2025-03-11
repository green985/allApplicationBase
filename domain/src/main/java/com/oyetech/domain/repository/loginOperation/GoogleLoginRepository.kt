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
//    fun signInWithGoogle()

    // for google sign in

    val userAutoLoginStateFlow: MutableStateFlow<Boolean>

    val googleAuthStateFlow: MutableStateFlow<GoogleAuthResponseData>
    val googleUserStateFlow: MutableStateFlow<GoogleUserResponseData>

    suspend fun signInWithGoogle()
    fun signInWithGoogleAnonymous()

    fun autoLoginOperation()
}