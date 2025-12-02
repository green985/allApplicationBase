package com.oyetech.remote.questionRemote

import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val googleLoginRepository: GoogleLoginRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = googleLoginRepository.googleUserStateFlow.value.uid ?: ""

        val tokenAnon =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVkdXdodXZnZGNhY3ZkaHpoZXlpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjIzNzIxNTgsImV4cCI6MjA3Nzk0ODE1OH0.4MzZwLMEZKdayHDwCq9Apk9Xs67WfW4PTCmFhcQtMP0"
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $tokenAnon")
            .build()
        return chain.proceed(newRequest)
    }
}