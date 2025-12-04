package com.oyetech.models.firebaseModels.googleAuth

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel

@Keep
data class GoogleUserPostData(
    val uid: String = "",
    val firebaseToken: String? = "",
    val email: String? = null,
    val displayName: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val providerId: String = "",
    val tenantId: String? = null,
    val isAnonymous: Boolean = false,
    val metadata: UserMetadata? = null,
    val providerData: List<ProviderDataInfo> = emptyList(),
)

fun GoogleUserResponseData.toGoogleUserPostData(): GoogleUserPostData {
    return GoogleUserPostData(
        uid = this.uid,
        firebaseToken = this.token,
        email = this.email,
        displayName = this.displayName,
        phoneNumber = this.phoneNumber,
        photoUrl = this.photoUrl,
        providerId = this.providerId,
        tenantId = this.tenantId,
        isAnonymous = this.isAnonymous,
        metadata = this.metadata,
        providerData = this.providerData,
    )
}

fun GoogleUserPostData.toGoogleUserResponseData(): GoogleUserResponseData {
    return GoogleUserResponseData(
        uid = this.uid,
        token = this.firebaseToken,
        email = this.email,
        displayName = this.displayName,
        phoneNumber = this.phoneNumber,
        photoUrl = this.photoUrl,
        providerId = this.providerId,
        tenantId = this.tenantId,
        isAnonymous = this.isAnonymous,
        metadata = this.metadata,
        providerData = this.providerData,
    )
}

@Keep
data class GoogleUserResponseData(
    val uid: String = "",
    val token: String? = "",
    val email: String? = null,
    val displayName: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val providerId: String = "",
    val tenantId: String? = null,
    val isAnonymous: Boolean = false,
    val metadata: UserMetadata? = null,
    val providerData: List<ProviderDataInfo> = emptyList(),
    @get:Exclude
    val errorException: Exception? = null, // Added field
) {
    fun toFirebaseUserProfileModel(): FirebaseUserProfileModel {
        return FirebaseUserProfileModel(
            userId = uid,
            isAnonymous = isAnonymous,
            creationTimestamp = metadata?.creationTimestamp ?: 0,
            lastSignInTimestamp = metadata?.lastSignInTimestamp,
            errorException = errorException,
        )
    }

    companion object {

        fun getNewWithException(message: String?): GoogleUserResponseData {
            if (message.isNullOrBlank()) {
                return GoogleUserResponseData(errorException = Exception(message))
            } else {
                return GoogleUserResponseData(errorException = Exception("exceptionError"))
            }
        }
    }
}

fun GoogleUserResponseData?.isUserAnonymous() = this?.isAnonymous ?: false

fun GoogleUserResponseData?.isUserHasUID(): Boolean = this?.uid?.isNotEmpty() ?: false

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

