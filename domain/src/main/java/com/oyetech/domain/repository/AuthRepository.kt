package com.oyetech.domain.repository

import com.oyetech.models.entity.auth.AuthRequestResponse
import com.oyetech.models.postBody.auth.AuthRequestBody
import com.oyetech.models.postBody.auth.FacebookSignUpRequestBody
import com.oyetech.models.postBody.auth.GoogleSignUpRequestBody
import com.oyetech.models.postBody.auth.IsNickUseRequestBody
import com.oyetech.models.postBody.auth.NonSocialSignUpRequestBody
import com.oyetech.models.postBody.auth.RefreshAuthRequestBody
import com.oyetech.models.postBody.auth.RegisterRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-20.02.2022-
-19:03-
 **/

interface AuthRepository {

    suspend fun getFirstRequestNAC(): Flow<Int>

    suspend fun getTokenDataResponse(params: AuthRequestBody): Flow<AuthRequestResponse>
    suspend fun postAuthIsValidNick(params: IsNickUseRequestBody): Flow<Boolean>
    suspend fun getAuthWithGoogle(params: GoogleSignUpRequestBody): Flow<AuthRequestResponse>
    suspend fun register(params: RegisterRequestBody): Flow<Boolean>
    suspend fun getTokenDataResponseWithRefresh(params: RefreshAuthRequestBody): Flow<AuthRequestResponse>
    suspend fun getAuthWithFacebook(params: FacebookSignUpRequestBody): Flow<AuthRequestResponse>
    suspend fun deleteAccount(): Flow<Boolean>
    suspend fun logoutUser(): Flow<Boolean>
    suspend fun logoutUserWithAllDevice(): Flow<Boolean>
    suspend fun getAuthWithNonSocial(params: NonSocialSignUpRequestBody): Flow<AuthRequestResponse>
}
