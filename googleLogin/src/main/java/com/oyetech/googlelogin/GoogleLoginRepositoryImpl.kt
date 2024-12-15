package com.oyetech.googlelogin

/**
Created by Erdi Özbek
-17.06.2024-
-00:49-
 **/
import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.models.firebaseModels.googleAuth.GoogleAuthResponseData
import kotlinx.coroutines.flow.MutableStateFlow

class GoogleLoginRepositoryImpl(
    private val activityProviderUseCase: ActivityProviderUseCase,
) : GoogleLoginRepository {
    override val googleAuthStateFlow =
        MutableStateFlow<GoogleAuthResponseData>(GoogleAuthResponseData())

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    private lateinit var activity: AppCompatActivity

    override fun controlUserAlreadySignIn() {
        activity = getActivityOrSetError("setupGoogleSignIn activity problem") ?: return
        val account = GoogleSignIn.getLastSignedInAccount(activity)

        if (account != null) {
            handleSignInResult(account)
        } else {
            googleAuthStateFlow.value =
                GoogleAuthResponseData(errorException = Exception("controlUserAlreadySignIn Google sign in failed"))
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
            googleAuthStateFlow.value =
                GoogleAuthResponseData(errorException = Exception("setupGoogleSignIn activity problem"))
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity!!.getString(R.string.firebase_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
    }

    private fun setupGoogleSignInLauncher() {
        if (activity == null) {
            googleAuthStateFlow.value =
                GoogleAuthResponseData(errorException = Exception("setupGoogleSignIn activity problem"))
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
                    googleAuthStateFlow.value =
                        GoogleAuthResponseData(errorException = Exception("setupGoogleSignInLauncher Google sign in failed"))
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
                        googleAuthStateFlow.value =
                            GoogleAuthResponseData(errorException = Exception("handleSignInResult Google sign in failed"))
                    }
                }
        }
    }

    private fun getActivityOrSetError(errorString: String): AppCompatActivity? {
        val activity = activityProviderUseCase.getCurrentActivity()

        if (activity == null) {
            googleAuthStateFlow.value =
                GoogleAuthResponseData(errorException = Exception(errorString))
            return null
        }
        return activity as? AppCompatActivity
    }

}
