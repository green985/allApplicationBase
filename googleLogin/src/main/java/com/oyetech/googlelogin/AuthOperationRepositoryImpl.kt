package com.oyetech.googlelogin

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
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
import com.oyetech.domain.repository.loginOperation.AuthOperationRepository
import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserResponseData
import com.oyetech.models.firebaseModels.googleAuth.isUserHasUID
import com.oyetech.models.firebaseModels.googleAuth.toGoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserDataProperty
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class AuthOperationRepositoryImpl(
    private val activityProviderUseCase: ActivityProviderUseCase,
    private val questionSupabaseRepository: QuestionSupabaseRepository,
    private val sharedOperationRepository: SharedOperationRepository,
) : AuthOperationRepository {

    override val userDataStateFlow = MutableStateFlow<UserDataProperty?>(
        sharedOperationRepository.getGoogleUserData()
    )

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loginWithGoogleAndSyncUser(): Result<UserDataProperty> {
        return try {
            val googleUser = signInWithGoogle()

            if (!googleUser.isUserHasUID()) {
                return Result.failure(
                    googleUser.errorException ?: Exception("Google sign in failed")
                )
            }

            val userData = loginOrRegisterUser(googleUser)

            sharedOperationRepository.saveGoogleUserData(userData)
            userDataStateFlow.value = userData
            Result.success(userData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun signInWithGoogle(): GoogleUserResponseData {
        val activity = activityProviderUseCase.getCurrentActivity() as? ComponentActivity
            ?: return GoogleUserResponseData(
                errorException = Exception("signInWithGoogle activity problem")
            )

        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(
                    "652520712669-5sudspef6cq60j7drtgr06rm567r0qa2.apps.googleusercontent.com"
                )
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = CredentialManager.create(activity).getCredential(activity, request)
            handleGoogleCredential(result)
        } catch (e: Exception) {
            GoogleUserResponseData(
                errorException = Exception(e.message ?: "Google sign in failed")
            )
        }
    }

    @Suppress("NestedBlockDepth")
    private fun handleGoogleCredential(result: GetCredentialResponse): GoogleUserResponseData {
        return when (val credential = result.credential) {
            is PublicKeyCredential -> {
                Timber.d("PublicKeyCredential received, not supported")
                GoogleUserResponseData(errorException = Exception("Unexpected credential"))
            }

            is PasswordCredential -> {
                Timber.d("PasswordCredential received, not supported")
                GoogleUserResponseData(errorException = Exception("Unexpected credential"))
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        if (idToken.isBlank()) {
                            GoogleUserResponseData(
                                errorException = Exception("Google sign in failed - empty token")
                            )
                        } else {
                            GoogleUserResponseData(
                                uid = googleIdTokenCredential.id,
                                token = idToken
                            )
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        GoogleUserResponseData(
                            errorException = Exception(e.message ?: "Invalid Google token")
                        )
                    }
                } else {
                    GoogleUserResponseData(errorException = Exception("Unexpected credential type"))
                }
            }

            else -> GoogleUserResponseData(errorException = Exception("Unexpected credential"))
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateUserProfile(
        username: String,
        age: String,
        gender: String,
    ): Result<UserDataProperty> {
        return try {
            val currentUser = userDataStateFlow.value
                ?: return Result.failure(Exception("User session not found"))

            val profileToUpdate = UserProfileProperty(
                token = currentUser.token,
                userId = currentUser.userId,
                username = username,
                age = age,
                gender = gender,
            )

            val updatedUser = questionSupabaseRepository.updateUser(profileToUpdate).first()
            sharedOperationRepository.saveGoogleUserData(updatedUser)
            userDataStateFlow.value = updatedUser
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val currentUser = userDataStateFlow.value
                ?: return Result.failure(Exception("User session not found"))

            questionSupabaseRepository.deleteAccount(currentUser.userId).first()
            sharedOperationRepository.removeGoogleUserData()
            userDataStateFlow.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun loginOrRegisterUser(googleUser: GoogleUserResponseData): UserDataProperty {
        return runCatching {
            questionSupabaseRepository
                .getUserWithToken(GetUserWithTokenBody(token = googleUser.token))
                .first()
        }.getOrElse {
            questionSupabaseRepository
                .registerGoogleUser(googleUser.toGoogleUserPostData())
                .first()
        }
    }
}

