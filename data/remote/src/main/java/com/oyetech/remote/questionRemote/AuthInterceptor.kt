package com.oyetech.remote.questionRemote

import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val firebaseUserRepository: FirebaseUserRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenAnon =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVkdXdodXZnZGNhY3ZkaHpoZXlpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjIzNzIxNTgsImV4cCI6MjA3Nzk0ODE1OH0.4MzZwLMEZKdayHDwCq9Apk9Xs67WfW4PTCmFhcQtMP0"


        val token = firebaseUserRepository.userProfileDataStateFlow.value.accessToken.ifBlank {
            tokenAnon
        }
//        Timber.d("AuthInterceptor Token: $token")
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}