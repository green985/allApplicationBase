package com.oyetech.kmpfeatures.auth

import com.oyetech.kmpfeatures.network.EndpointStrings
import com.oyetech.kmpfeatures.network.bodyOrError
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.state.LoginUiState

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GoogleLoginOperation(
    private val httpClient: HttpClient,
    private val identityProvider: GoogleIdentityProvider,
) {
    val supportsSessions: Boolean = identityProvider.supportsSessions
    private val mutableSession = MutableStateFlow(LoginUiState(isLoading = supportsSessions))
    val session = mutableSession.asStateFlow()
    val sessionKey: String
        get() = "${session.value.isAdmin}:${session.value.userId}"
    private var stopObserving: (() -> Unit)? = null

    suspend fun startSession() {
        if (!supportsSessions) return
        if (stopObserving == null) {
            stopObserving = identityProvider.observeSession(::updateSession)
        }
        identityProvider.sessionAction(GoogleSessionAction.Restore).fold(
            onSuccess = { updateSession(it, "") },
            onFailure = {
                mutableSession.update { state ->
                    state.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = StringKeys.sessionUnavailable
                    )
                }
            },
        )
    }

    fun stopSession() {
        stopObserving?.invoke()
        stopObserving = null
    }

    suspend fun selectAdmin(): Result<Unit> {
        if (!supportsSessions) return Result.success(Unit)
        return identityProvider.sessionAction(GoogleSessionAction.Admin).mapCatching { user ->
            checkNotNull(user) { StringKeys.sessionUnavailable }
            updateSession(user, "")
        }
    }

    suspend fun currentUser(rejectedToken: String? = null): AuthenticatedUser {
        val action =
            if (rejectedToken == null) GoogleSessionAction.Token else GoogleSessionAction.Refresh
        return checkNotNull(
            identityProvider.sessionAction(action, rejectedToken = rejectedToken.orEmpty())
                .getOrThrow(),
        ) { StringKeys.sessionExpired }
    }

    suspend fun expireSession(rejectedToken: String) {
        val user = identityProvider.sessionAction(
            GoogleSessionAction.Expire,
            rejectedToken = rejectedToken,
        ).getOrThrow()
        if (user != null) {
            updateSession(user, "")
        } else {
            mutableSession.update {
                LoginUiState(requiresLogin = true, errorMessage = StringKeys.sessionExpired)
            }
        }
    }

    private fun updateSession(
        user: AuthenticatedUser?,
        error: String,
        requiresLogin: Boolean = false,
    ) {
        mutableSession.update { previous ->
            if (requiresLogin) {
                LoginUiState(requiresLogin = true, errorMessage = StringKeys.sessionExpired)
            } else if (error.isNotEmpty()) {
                previous.copy(isLoading = false, isError = true, errorMessage = error)
            } else {
                LoginUiState(
                    isAuthenticated = user != null,
                    userId = user?.userId.orEmpty(),
                    username = user?.username.orEmpty(),
                    isAdmin = user?.isAdmin == true,
                    requiresLogin = user == null && (previous.isAuthenticated || previous.requiresLogin),
                    errorMessage = if (user == null && previous.isAuthenticated) StringKeys.sessionExpired else "",
                )
            }
        }
    }

    suspend fun login(clientId: String): Result<AuthenticatedUser> {
        if (supportsSessions) {
            return identityProvider.sessionAction(GoogleSessionAction.Login, clientId)
                .mapCatching { user ->
                    checkNotNull(user) { StringKeys.googleLoginFailed }
                    updateSession(user, "")
                    user
                }
        }
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
        }.onFailure { if (it is CancellationException) throw it }
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
