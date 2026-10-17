package com.oyetech.kmpfeatures.auth

expect class GoogleIdentityProvider() {
    val supportsSessions: Boolean
    suspend fun requestIdToken(clientId: String): Result<GoogleIdentityToken>
    suspend fun sessionAction(
        action: GoogleSessionAction,
        clientId: String = "",
        rejectedToken: String = "",
    ): Result<AuthenticatedUser?>

    fun observeSession(onChange: (AuthenticatedUser?, String, Boolean) -> Unit): () -> Unit
}

data class GoogleIdentityToken(
    val uid: String,
    val token: String,
    val nonce: String,
)
