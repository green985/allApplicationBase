package com.oyetech.kmpfeatures.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GoogleLoginOperation(
    private val httpClient: HttpClient,
    private val identityProvider: GoogleIdentityProvider,
) {
    suspend fun login(clientId: String): Result<AuthenticatedUser> {
        return runCatching {
            val googleCredential = identityProvider.requestIdToken(clientId).getOrThrow()
            val existingUser = runCatching {
                httpClient.post("$baseUrl/v1/getUserWithToken") {
                    contentType(ContentType.Application.Json)
                    setBody(UserWithTokenRequest(token = googleCredential.token))
                }.body<GoogleApiResponse<AuthenticatedUser>>()
            }.getOrNull()

            existingUser?.data ?: httpClient.post("$baseUrl/v1/registerGoogleUser") {
                contentType(ContentType.Application.Json)
                setBody(
                    GoogleUserRequest(
                        uid = googleCredential.uid,
                        token = googleCredential.token,
                    ),
                )
            }.body<GoogleApiResponse<AuthenticatedUser>>().data
                ?: error(existingUser?.message?.ifBlank { "Google login failed" } ?: "Google login failed")
        }
    }

    private companion object {
        const val baseUrl = "https://uduwhuvgdcacvdhzheyi.supabase.co/functions"
    }
}
