package com.oyetech.kmpfeatures.auth

actual class GoogleIdentityProvider {
    actual suspend fun requestIdToken(clientId: String): Result<GoogleIdentityToken> {
        return Result.failure(
            UnsupportedOperationException("Android Google login is not connected to kmpFeatures yet"),
        )
    }
}
