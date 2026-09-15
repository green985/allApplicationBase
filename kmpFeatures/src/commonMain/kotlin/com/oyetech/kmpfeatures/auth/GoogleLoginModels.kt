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
data class GoogleApiResponse<T>(
    val data: T? = null,
    val message: String = "",
    val status: Boolean = false,
)

@Serializable
data class AuthenticatedUser(
    val token: String = "",
    val accessToken: String = "",
    val userId: String = "",
    val username: String = "",
)
