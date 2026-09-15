package com.oyetech.kmpfeatures.auth

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
actual class GoogleIdentityProvider {
    actual suspend fun requestIdToken(clientId: String): Result<GoogleIdentityToken> =
        suspendCancellableCoroutine { continuation ->
            startGoogleLogin(clientId) { uid, token, nonce, error ->
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
        }
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
private fun startGoogleLogin(
    clientId: String,
    callback: (uid: String, token: String, nonce: String, error: String) -> Unit,
): Unit = js(
    """
    {
        if (!window.google || !window.google.accounts || !window.google.accounts.id) {
            callback("", "", "", "Google Identity Services yüklenemedi");
            return;
        }
        const nonce = crypto.randomUUID();
        crypto.subtle.digest(
            "SHA-256",
            new TextEncoder().encode(nonce),
        ).then((digest) => {
            const hashedNonce = Array.from(new Uint8Array(digest))
                .map((byte) => byte.toString(16).padStart(2, "0"))
                .join("");

            window.google.accounts.id.initialize({
                client_id: clientId,
                nonce: hashedNonce,
                callback: (response) => {
                    const token = response.credential || "";
                    try {
                        const encodedPayload = token
                            .split('.')[1]
                            .replace(/-/g, '+')
                            .replace(/_/g, '/');
                        const paddedPayload =
                            encodedPayload + '='.repeat((4 - encodedPayload.length % 4) % 4);
                        const payload = token ? JSON.parse(atob(paddedPayload)) : {};
                        callback(payload.sub || "", token, nonce, "");
                    } catch (error) {
                        callback("", "", "", "Google token okunamadı");
                    }
                }
            });
            window.google.accounts.id.prompt((notification) => {
                if (notification.isNotDisplayed()) {
                    callback(
                        "",
                        "",
                        "",
                        notification.getNotDisplayedReason() || "Google login gösterilemedi",
                    );
                } else if (notification.isSkippedMoment()) {
                    callback("", "", "", "Google login iptal edildi");
                }
            });
        }).catch(() => {
            callback("", "", "", "Google nonce oluşturulamadı");
        });
    }
    """,
)
