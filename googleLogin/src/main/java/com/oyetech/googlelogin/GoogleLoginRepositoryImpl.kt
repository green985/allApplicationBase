package com.oyetech.googlelogin

/**
Created by Erdi Özbek
-17.06.2024-
-00:49-
 **/
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData.Companion.getNewWithException
import com.oyetech.models.firebaseModels.googleAuth.ProviderDataInfo
import com.oyetech.models.firebaseModels.googleAuth.UserMetadata
import com.oyetech.models.firebaseModels.googleAuth.isUserHasUID
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class GoogleLoginRepositoryImpl(
    private val activityProviderUseCase: ActivityProviderUseCase,
    private val firebaseUserRepository: FirebaseUserRepository,
) : GoogleLoginRepository {
    override val googleUserStateFlow =
        MutableStateFlow(GoogleUserResponseData())
    override val userAutoLoginStateFlow =
        MutableStateFlow(false)

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var activity: ComponentActivity

    override fun signInWithGoogleAnonymous() {
        activity = getActivityOrSetError("setupGoogleSignIn activity problem") ?: return
        try {
            googleUserStateFlow.value = GoogleUserResponseData()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val userGoogleData = getCurrentUserResponse()
                            if (userGoogleData?.isUserHasUID() == true) {
                                googleUserStateFlow.value = userGoogleData
                            } else {
                                googleUserStateFlow.value =
                                    getNewWithException("setupGoogleSignInLauncher Google sign in failed")
                            }
                        } else {
                            googleUserStateFlow.value =
                                getNewWithException("setupGoogleSignInLauncher Google sign in failed")
                        }
                    }
            } else {
                val userGoogleData = getCurrentUserResponse()
                if (userGoogleData == null || !userGoogleData.isUserHasUID()) {
                    googleUserStateFlow.value =
                        getNewWithException("setupGoogleSignInLauncher Google sign in failed")
                } else {
                    googleUserStateFlow.value = userGoogleData.copy()
                }
            }
        } catch (e: Exception) {
            googleUserStateFlow.value = getNewWithException(e.message)
        }

    }

    override suspend fun signInWithGoogle() {
        activity = getActivityOrSetError("setupGoogleSignIn activity problem") ?: return
        try {
            googleUserStateFlow.value = GoogleUserResponseData()
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                signWithGoogle()
            } else {
                val userGoogleData = getCurrentUserResponse()
                if (userGoogleData == null || !userGoogleData.isUserHasUID()) {
                    googleUserStateFlow.value =
                        getNewWithException("setupGoogleSignInLauncher Google sign in failed")
                } else {
                    googleUserStateFlow.value = userGoogleData.copy()
                }
            }
        } catch (e: Exception) {
            googleUserStateFlow.value = getNewWithException(e.message)
        }

    }

    private suspend fun signWithGoogle(autoLoginCheck: Boolean = false) {
        try {
            // Instantiate a Google sign-in request
            val googleIdOption = GetGoogleIdOption.Builder()
                // Your server's client ID, not your Android client ID.
                // todo will be change....
                .setServerClientId("652520712669-5sudspef6cq60j7drtgr06rm567r0qa2.apps.googleusercontent.com")
                // Only show accounts previously used to sign in.

                .build()

// Create the Credential Manager request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credential =
                CredentialManager.Companion.create(activity).getCredential(activity, request)
            handleGoogleCriential(credential)
        } catch (e: Exception) {
            googleUserStateFlow.value = getNewWithException(e.message)
        }
    }

    private fun handleGoogleCriential(
        result: GetCredentialResponse,
    ) {

        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
//                responseJson = credential.authenticationResponseJson
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        // You can use the members of googleIdTokenCredential directly for UX
                        // purposes, but don't use them to store or control access to user
                        // data. For that you first need to validate the token:
                        // pass googleIdTokenCredential.getIdToken() to the backend server.
                        val idToken = googleIdTokenCredential.idToken

                        firebaseAuthWithGoogle(idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Timber.d("Received an invalid google id token response")
                    }
                } else {
                    Timber.d("Unexpected type of credential")
                }
            }

            else -> {
                Timber.d("Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userGoogleData = getCurrentUserResponse()

                if (userGoogleData?.isUserHasUID() == true) {
                    googleUserStateFlow.value = userGoogleData
                } else {
                    googleUserStateFlow.value =
                        getNewWithException("setupGoogleSignInLauncher Google sign in failed")
                }
            } else {
                googleUserStateFlow.value =
                    getNewWithException("setupGoogleSignInLauncher Google sign in failed")
            }
        }
    }

    override fun autoLoginOperation() {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                // do nothing.
                userAutoLoginStateFlow.value = true
            } else {
                val userGoogleData = getCurrentUserResponse()
                if (userGoogleData == null || !userGoogleData.isUserHasUID()) {
                    // maybe exception but not neccesserry...
                    userAutoLoginStateFlow.value = true
                } else {
                    val firebaseProfileUserModel = userGoogleData.toFirebaseUserProfileModel()
                    firebaseUserRepository.getUserProfileForAutoLogin(firebaseProfileUserModel) {
                        userAutoLoginStateFlow.value = true
                    }
                }
            }
        } catch (e: Exception) {
            // do nothing...
            userAutoLoginStateFlow.value = true
        }

    }

    override fun removeUser(uid: String) {
        firebaseAuth.signOut()
    }

    override fun getUserUid(): String {
        return firebaseAuth.currentUser?.uid ?: googleUserStateFlow.value.uid
    }

    fun getCurrentUserResponse(
    ): GoogleUserResponseData? {

        val currentUser = firebaseAuth.currentUser
        val userGoogleData = currentUser?.toGoogleUserResponseData()

        return userGoogleData
    }

    private fun getActivityOrSetError(errorString: String): ComponentActivity? {
        val activity = activityProviderUseCase.getCurrentActivity()

        if (activity == null) {
            googleUserStateFlow.value = getNewWithException(errorString)
            return null
        }
        return activity as? ComponentActivity
    }

    fun FirebaseUser.toGoogleUserResponseData(): GoogleUserResponseData {
        return GoogleUserResponseData(
            uid = this.uid,
            email = this.email,
            displayName = this.displayName,
            phoneNumber = this.phoneNumber,
            photoUrl = this.photoUrl?.toString(),
            providerId = this.providerId,
            tenantId = this.tenantId,
            isAnonymous = this.isAnonymous,
            metadata = this.getMetadata()?.let {
                UserMetadata(
                    creationTimestamp = it.creationTimestamp,
                    lastSignInTimestamp = it.lastSignInTimestamp
                )
            },
            providerData = this.providerData.map {
                ProviderDataInfo(
                    providerId = it.providerId,
                    uid = it.uid,
                    displayName = it.displayName,
                    email = it.email,
                    phoneNumber = it.phoneNumber,
                    photoUrl = it.photoUrl?.toString()
                )
            }
        )
    }
}
