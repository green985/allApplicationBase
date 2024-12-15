package com.oyetech.domain.useCases

import com.oyetech.domain.repository.AuthRepository
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
-19:08-
 **/

class AuthOperationUseCase(private val authRepository: AuthRepository) {

    suspend fun getFirstRequestToNac(): Flow<Int> {
        return authRepository.getFirstRequestNAC()
    }

    fun normalReturnFuck(): Int {
        return 666
    }

    suspend fun getAuthRequestResponse(params: AuthRequestBody): Flow<AuthRequestResponse> {
        return authRepository.getTokenDataResponse(params)
    }

    suspend fun getTokenDataResponseWithRefresh(params: RefreshAuthRequestBody): Flow<AuthRequestResponse> {
        return authRepository.getTokenDataResponseWithRefresh(params)
    }

    suspend fun logoutUser(): Flow<Boolean> {
        return authRepository.logoutUser()
    }

    suspend fun logoutUserWithAllDevice(): Flow<Boolean> {
        return authRepository.logoutUserWithAllDevice()
    }

    suspend fun getAuthWithGoogle(params: GoogleSignUpRequestBody): Flow<AuthRequestResponse> {
        return authRepository.getAuthWithGoogle(params)
    }

    suspend fun getAuthWithNonSocial(params: NonSocialSignUpRequestBody): Flow<AuthRequestResponse> {
        return authRepository.getAuthWithNonSocial(params)
    }

    suspend fun getAuthWithFacebook(params: FacebookSignUpRequestBody): Flow<AuthRequestResponse> {
        return authRepository.getAuthWithFacebook(params)
    }

    suspend fun postAuthIsValidNick(params: IsNickUseRequestBody): Flow<Boolean> {
        return authRepository.postAuthIsValidNick(params)
    }

    suspend fun register(params: RegisterRequestBody): Flow<Boolean> {
        return authRepository.register(params)
    }

    suspend fun deleteAccount(): Flow<Boolean> {
        return authRepository.deleteAccount()
    }
}
