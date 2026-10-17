package com.oyetech.kmpfeatures.auth

import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserRequest(
    val uid: String,
    val token: String,
    val nonce: String,
)

@Serializable
data class UserWithTokenRequest(
    val token: String,
)

@Serializable
data class AuthenticatedUser(
    val token: String = "",
    val accessToken: String = "",
    val userId: String = "",
    val username: String? = "",
    val isAdmin: Boolean = false,
)

enum class GoogleSessionAction {
    Restore, Login, Admin, Token, Refresh, Expire,
}

@Serializable
internal data class GoogleSessionResponse(
    val user: AuthenticatedUser? = null,
    val error: String = "",
    val requiresLogin: Boolean = false,
)
