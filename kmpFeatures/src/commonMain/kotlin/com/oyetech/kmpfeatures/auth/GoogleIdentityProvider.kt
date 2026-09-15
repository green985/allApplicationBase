package com.oyetech.kmpfeatures.auth

expect class GoogleIdentityProvider() {
    suspend fun requestIdToken(clientId: String): Result<GoogleIdentityToken>
}

data class GoogleIdentityToken(
    val uid: String,
    val token: String,
    val nonce: String,
)
