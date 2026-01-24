package com.oyetech.remote.questionRemote

import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class AuthInterceptor(
    private val firebaseUserRepository: FirebaseUserRepository,
    private val googleLoginRepository: GoogleLoginRepository,
    private val sharedOperationRepository: SharedOperationRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenAnon =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVkdXdodXZnZGNhY3ZkaHpoZXlpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjIzNzIxNTgsImV4cCI6MjA3Nzk0ODE1OH0.4MzZwLMEZKdayHDwCq9Apk9Xs67WfW4PTCmFhcQtMP0"

        val googleToken = sharedOperationRepository.getGoogleUserData()?.token ?: ""
//        val token = firebaseUserRepository.userProfileDataStateFlow.value.accessToken.ifBlank {
        val token = googleToken.ifBlank {
            tokenAnon
        }
        Timber.d("AuthInterceptor Token: $token")
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}