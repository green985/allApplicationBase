package com.oyetech.domain.repository.loginOperation

import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
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

    val googleUserStateFlow: MutableStateFlow<GoogleUserResponseData>

    val googleUserDataStateFlow: MutableStateFlow<UserProfileProperty?>

    suspend fun signInWithGoogle()

    fun autoLoginOperation()
    fun autoLoginOperation2()
    fun removeUser(uid: String)
    fun getUserUid(): String
}
