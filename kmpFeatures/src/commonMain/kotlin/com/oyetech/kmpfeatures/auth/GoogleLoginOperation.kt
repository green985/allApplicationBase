package com.oyetech.kmpfeatures.auth

import com.oyetech.kmpfeatures.network.EndpointStrings
import com.oyetech.kmpmodels.stringKeys.StringKeys

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

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

            existingUser?.data ?: postAndLog<AuthenticatedUser>(
                endpoint = EndpointStrings.registerGoogleUser,
                request = GoogleUserRequest(
                    uid = googleCredential.uid,
                    token = googleCredential.token,
                    nonce = googleCredential.nonce,
                ),
            ).data
            ?: error(
                existingUser?.message?.ifBlank { StringKeys.googleLoginFailed }
                    ?: StringKeys.googleLoginFailed,
            )
        }
    }

    private suspend inline fun <reified T> postAndLog(
        endpoint: String,
        request: Any,
    ): GoogleApiResponse<T> {
        val response: HttpResponse = httpClient.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val rawBody = response.bodyAsText()
        println(
            "GoogleLogin response endpoint=$endpoint " +
                    "status=${response.status.value} " +
                    "headers=${response.headers.entries()} " +
                    "body=$rawBody",
        )
        return json.decodeFromString(rawBody)
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}
