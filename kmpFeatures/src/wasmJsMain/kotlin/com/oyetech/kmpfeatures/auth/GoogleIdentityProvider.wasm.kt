package com.oyetech.kmpfeatures.auth

import com.oyetech.kmpfeatures.network.KMPAuthConfig
import com.oyetech.kmpmodels.stringKeys.StringKeys
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
actual class GoogleIdentityProvider {
    actual val supportsSessions: Boolean = true

    actual suspend fun sessionAction(
        action: GoogleSessionAction,
        clientId: String,
        rejectedToken: String,
    ): Result<AuthenticatedUser?> = suspendCancellableCoroutine { continuation ->
        val requestId = invokeSession(
            action.name,
            clientId,
            rejectedToken,
            KMPAuthConfig.supabaseUrl,
            KMPAuthConfig.anonToken,
        ) { payload ->
            if (continuation.isActive) {
                val response = runCatching { Json.decodeFromString<GoogleSessionResponse>(payload) }
                    .mapCatching {
                        check(it.error.isEmpty()) { sessionError(it.error) }
                        it.user
                    }
                continuation.resume(response)
            }
        }
        continuation.invokeOnCancellation { cancelSessionRequest(requestId) }
    }

    actual fun observeSession(onChange: (AuthenticatedUser?, String, Boolean) -> Unit): () -> Unit {
        val subscriptionId = observeWebSession { payload ->
            val response = runCatching { Json.decodeFromString<GoogleSessionResponse>(payload) }
            response.fold(
                onSuccess = {
                    onChange(
                        it.user,
                        if (it.error.isEmpty()) "" else sessionError(it.error),
                        it.requiresLogin,
                    )
                },
                onFailure = { onChange(null, StringKeys.sessionUnavailable, false) },
            )
        }
        return { stopObservingWebSession(subscriptionId) }
    }

    private fun sessionError(code: String): String = when (code) {
        "login_cancelled" -> StringKeys.googleLoginCancelled
        "session_expired" -> StringKeys.sessionExpired
        else -> StringKeys.sessionUnavailable
    }

    actual suspend fun requestIdToken(clientId: String): Result<GoogleIdentityToken> =
        suspendCancellableCoroutine { continuation ->
            val requestId = startGoogleLogin(clientId) { uid, token, nonce, error ->
                if (!continuation.isActive) return@startGoogleLogin
                if (token.isNotBlank()) {
                    continuation.resume(
                        Result.success(
                            GoogleIdentityToken(
                                uid = uid,
                                token = token,
                                nonce = nonce,
                            ),
                        ),
                    )
                } else {
                    continuation.resume(
                        Result.failure(Exception(error.ifBlank { "Google login failed" })),
                    )
                }
            }
            continuation.invokeOnCancellation { cancelGoogleLogin(requestId) }
        }
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun invokeSession(
    action: String,
    clientId: String,
    rejectedToken: String,
    url: String,
    key: String,
    callback: (String) -> Unit,
): Int = js(
    """{ return window.KMP_AUTH.request(action, clientId, rejectedToken, url, key, callback); }""",
)

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun cancelSessionRequest(id: Int): Unit = js("""{ window.KMP_AUTH.cancelRequest(id); }""")

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun observeWebSession(callback: (String) -> Unit): Int =
    js("""{ return window.KMP_AUTH.subscribe(callback); }""")

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun stopObservingWebSession(id: Int): Unit =
    js("""{ window.KMP_AUTH.unsubscribe(id); }""")

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun startGoogleLogin(
    clientId: String,
    callback: (uid: String, token: String, nonce: String, error: String) -> Unit,
): Int = js("""{ return window.KMP_AUTH.googleCredential(clientId, callback); }""")

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun cancelGoogleLogin(id: Int): Unit =
    js("""{ window.KMP_AUTH.cancelGoogleCredential(id); }""")
