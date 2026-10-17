package com.oyetech.kmpfeatures.auth

actual class GoogleIdentityProvider {
    actual val supportsSessions: Boolean = false

    actual suspend fun sessionAction(
        action: GoogleSessionAction,
        clientId: String,
        rejectedToken: String,
    ): Result<AuthenticatedUser?> = Result.failure(
        UnsupportedOperationException("Web sessions are not available on Android"),
    )

    actual fun observeSession(onChange: (AuthenticatedUser?, String, Boolean) -> Unit): () -> Unit =
        {}

    actual suspend fun requestIdToken(clientId: String): Result<GoogleIdentityToken> {
        return Result.failure(
            UnsupportedOperationException("Android Google login is not connected to kmpFeatures yet"),
        )
    }
}
