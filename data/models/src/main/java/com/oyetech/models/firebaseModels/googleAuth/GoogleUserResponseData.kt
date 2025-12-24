package com.oyetech.models.firebaseModels.googleAuth

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude

@Keep
data class GetUserWithTokenBody(
    val firebaseToken: String = "",
)

@Keep
data class GoogleUserPostData(
    val uid: String = "",
    val firebaseToken: String? = "",
)

fun GoogleUserResponseData.toGoogleUserPostData(): GoogleUserPostData {
    return GoogleUserPostData(
        uid = this.uid,
        firebaseToken = this.token,
    )
}

@Keep
data class GoogleUserResponseData(
    val uid: String = "",
    val token: String? = "",
    @get:Exclude
    val errorException: Exception? = null,
) {
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

fun GoogleUserResponseData?.isUserHasUID(): Boolean = this?.uid?.isNotEmpty() ?: false

