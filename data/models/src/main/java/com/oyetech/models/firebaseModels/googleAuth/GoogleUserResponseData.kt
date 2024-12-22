package com.oyetech.models.firebaseModels.googleAuth

data class GoogleUserResponseData(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val providerId: String = "",
    val tenantId: String? = null,
    val isAnonymous: Boolean = false,
    val metadata: UserMetadata? = null,
    val providerData: List<ProviderDataInfo> = emptyList(),
    val errorException: Exception? = null, // Added field
    val errorMessage: String? = errorException?.message, // Added field
) {
}

fun GoogleUserResponseData?.isUserAnonymous() = this?.isAnonymous ?: false

fun GoogleUserResponseData?.isUserLogin(): Boolean = this?.uid?.isNotEmpty() ?: false

data class UserMetadata(
    val creationTimestamp: Long? = null,
    val lastSignInTimestamp: Long? = null,
)

data class ProviderDataInfo(
    val providerId: String = "",
    val uid: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
)

