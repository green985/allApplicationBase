package com.oyetech.googlelogin

import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData.Companion.getNewWithException
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class GoogleLoginRepositoryImpl3(
    private val activityProviderUseCase: ActivityProviderUseCase,
    private val sharedOperationRepository: SharedOperationRepository,
) : GoogleLoginRepository {

    override val googleUserStateFlow = MutableStateFlow(GoogleUserResponseData())
    override val userAutoLoginStateFlow = MutableStateFlow(false)
    override val googleUserDataStateFlow = MutableStateFlow<UserProfileProperty?>(null)

    private lateinit var activity: ComponentActivity
    private var currentGoogleToken: String? = null
    private var currentUserId: String? = null

    override suspend fun signInWithGoogle() {
        activity = getActivityOrSetError("signInWithGoogle activity problem") ?: return
        try {
            googleUserStateFlow.value = GoogleUserResponseData()
            signWithGoogle()
        } catch (e: Exception) {
            googleUserStateFlow.value = getNewWithException(e.message)
        }
    }

    private suspend fun signWithGoogle() {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("652520712669-5sudspef6cq60j7drtgr06rm567r0qa2.apps.googleusercontent.com")
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credential = CredentialManager.create(activity).getCredential(activity, request)
            handleGoogleCredential(credential)
        } catch (e: Exception) {
            e.printStackTrace()
            googleUserStateFlow.value = getNewWithException(e.message)
        }
    }

    private fun handleGoogleCredential(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                Timber.d("PublicKeyCredential received, not supported")
            }

            is PasswordCredential -> {
                Timber.d("PasswordCredential received, not supported")
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        Timber.d("Google ID Token received: ${idToken.take(20)}...")

                        if (idToken.isNotBlank()) {
                            currentGoogleToken = idToken
                            currentUserId = googleIdTokenCredential.id
                            Timber.d("Google User ID: ${googleIdTokenCredential.data}")
                            // print all bundle data
                            for (key in googleIdTokenCredential.data.keySet()) {
                                val value = googleIdTokenCredential.data.get(key)
                                Timber.d("Data Key: $key Value: $value")
                            }

                            googleUserStateFlow.value = GoogleUserResponseData(
                                uid = googleIdTokenCredential.id,
                                token = idToken
                            )
                        } else {
                            googleUserStateFlow.value =
                                getNewWithException("Google sign in failed - empty token")
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Timber.e("Invalid google id token response: ${e.message}")
                        googleUserStateFlow.value = getNewWithException("Invalid Google token")
                    }
                } else {
                    Timber.d("Unexpected credential type: ${credential.type}")
                    googleUserStateFlow.value = getNewWithException("Unexpected credential type")
                }
            }

            else -> {
                Timber.d("Unexpected credential type")
                googleUserStateFlow.value = getNewWithException("Unexpected credential")
            }
        }
    }

    override fun autoLoginOperation() {
        userAutoLoginStateFlow.value = true
    }

    override fun autoLoginOperation2() {
        val savedUserData = sharedOperationRepository.getGoogleUserData()
        if (savedUserData != null) {
            googleUserDataStateFlow.value = savedUserData
        }
        userAutoLoginStateFlow.value = true
    }

    override fun removeUser(uid: String) {
        currentGoogleToken = null
        currentUserId = null
        sharedOperationRepository.removeGoogleUserData()
        googleUserDataStateFlow.value = null
        googleUserStateFlow.value = GoogleUserResponseData()
    }

    override fun getUserUid(): String {
        return currentUserId ?: googleUserStateFlow.value.uid
    }

    fun getCurrentGoogleToken(): String? {
        return currentGoogleToken
    }

    private fun getActivityOrSetError(errorString: String): ComponentActivity? {
        val activity = activityProviderUseCase.getCurrentActivity()

        if (activity == null) {
            googleUserStateFlow.value = getNewWithException(errorString)
            return null
        }
        return activity as? ComponentActivity
    }
}
