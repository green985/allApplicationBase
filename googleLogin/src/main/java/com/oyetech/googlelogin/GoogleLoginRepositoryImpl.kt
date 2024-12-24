package com.oyetech.googlelogin

/**
Created by Erdi Özbek
-17.06.2024-
-00:49-
 **/
import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.models.firebaseModels.googleAuth.GoogleAuthResponseData
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData.Companion.getNewWithException
import com.oyetech.models.firebaseModels.googleAuth.ProviderDataInfo
import com.oyetech.models.firebaseModels.googleAuth.UserMetadata
import com.oyetech.models.firebaseModels.googleAuth.isUserLogin
import kotlinx.coroutines.flow.MutableStateFlow

class GoogleLoginRepositoryImpl(
    private val activityProviderUseCase: ActivityProviderUseCase,
) : GoogleLoginRepository {
    override val googleAuthStateFlow =
        MutableStateFlow(GoogleAuthResponseData())

    override val googleUserStateFlow =
        MutableStateFlow(GoogleUserResponseData())

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

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
                            if (userGoogleData?.isUserLogin() == true) {
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
                if (userGoogleData == null || !userGoogleData.isUserLogin()) {
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

    fun getCurrentUserResponse(
    ): GoogleUserResponseData? {

        val currentUser = firebaseAuth.currentUser
        val userGoogleData = currentUser?.toGoogleUserResponseData()

        return userGoogleData
    }

    override fun controlUserAlreadySignIn() {
        activity = getActivityOrSetError("setupGoogleSignIn activity problem") ?: return
        val account = GoogleSignIn.getLastSignedInAccount(activity)

        if (account != null) {
            handleSignInResult(account)
        } else {
            googleUserStateFlow.value =
                getNewWithException("controlUserAlreadySignIn Google sign in failed")
        }
    }

    override fun signInWithGoogle() {
        googleAuthStateFlow.value = GoogleAuthResponseData(isLoading = true)


        activity = getActivityOrSetError("setupGoogleSignIn activity problem") ?: return
        setupGoogleSignIn()
        setupGoogleSignInLauncher()
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun setupGoogleSignIn() {
        if (activity == null) {
            googleUserStateFlow.value =
                getNewWithException("setupGoogleSignIn activity problem")
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity!!.getString(R.string.firebase_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
    }

    private fun setupGoogleSignInLauncher() {
        if (activity == null) {
            googleUserStateFlow.value =
                getNewWithException("setupGoogleSignIn activity problem")
            return
        }
        googleSignInLauncher = activityProviderUseCase!!.registerActivityResultLauncher(
            activity = activity,
            contract = ActivityResultContracts.StartActivityForResult(),
            callback = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task.result)
                } else {
                    googleUserStateFlow.value =
                        getNewWithException("setupGoogleSignInLauncher Google sign in failed")
                }
            }
        )
    }

    private fun handleSignInResult(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        activity?.let {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign-in success
                        val user = firebaseAuth.currentUser
                        // Do something with the user object
                        googleAuthStateFlow.value =
                            GoogleAuthResponseData(
                                id = user?.uid,
                                email = user?.email,
                                name = user?.displayName,
                                timeStamp = System.currentTimeMillis()
                            )
                    } else {
                        googleUserStateFlow.value =
                            getNewWithException("handleSignInResult Google sign in failed")
                    }
                }
        }
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
