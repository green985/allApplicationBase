package com.oyetech.kmpfeatures.auth

import com.oyetech.kmpfeatures.network.EndpointStrings
import com.oyetech.kmpfeatures.network.bodyOrError

import io.ktor.client.HttpClient
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
                postAndLog<AuthenticatedUser>(
                    endpoint = EndpointStrings.getUserWithToken,
                    request = UserWithTokenRequest(token = googleCredential.token),
                )
            }.getOrNull()

            existingUser ?: postAndLog<AuthenticatedUser>(
                endpoint = EndpointStrings.registerGoogleUser,
                request = GoogleUserRequest(
                    uid = googleCredential.uid,
                    token = googleCredential.token,
                    nonce = googleCredential.nonce,
                ),
            )
        }
    }

    private suspend inline fun <reified T> postAndLog(
        endpoint: String,
        request: Any,
    ): T {
        val response = httpClient.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        println(
            "GoogleLogin response endpoint=$endpoint " +
                    "status=${response.status.value} " +
                    "headers=${response.headers.entries()}",
        )
        return response.bodyOrError()
    }
}
