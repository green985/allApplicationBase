package com.oyetech.googlesignup.signUp;

import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.repository.firebase.FirebaseDBUserOperationRepository
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.models.firebaseModels.userModel.FirebaseUserModel
import com.oyetech.models.utils.states.ViewState

/**
Created by Erdi Özbek
-23.06.2024-
-16:59-
 **/

class GoogleSignUpVM(
    appDispatchers: AppDispatchers,
    private val googleLoginRepository: GoogleLoginRepository,
    private val firebaseDBUserOperationRepository: FirebaseDBUserOperationRepository,
    private val sharedHelperRepository: SharedHelperRepository,
) : BaseViewModel(appDispatchers) {

    val signInOperationLiveData = makeViewStateLiveData<FirebaseUserModel>()

    init {

        googleLoginRepository.googleAuthStateFlow.onEachWithTryCatch {
            if (it.isError) {
                signInOperationLiveData.postValue(
                    ViewState.error(
                        it.errorMessage ?: WallpaperLanguage.DEFAULT_ERROR
                    )
                )
            }
            if (it.isLoading) {
                signInOperationLiveData.postValue(ViewState.loading())
            }
        }

        firebaseDBUserOperationRepository.userDataStateFlow.onEachWithTryCatch {
            val firebaseUserOperationResponse = it
            if (it != null) {
                signInOperationLiveData.postValue(ViewState.success(firebaseUserOperationResponse))
            } else {
                signInOperationLiveData.postValue(ViewState.error(WallpaperLanguage.GOOGLE_SIGN_IN_ERROR))
            }
        }

        controlUserSignInLocally()
    }

    fun controlUserSignInLocally() {
        val isUserLoggedIn = sharedHelperRepository.getUserSignInLocally()
        if (isUserLoggedIn) {
            googleLoginRepository.controlUserAlreadySignIn()
        } else {
            // do nothing right now we will decide later
        }
    }

    fun googleSignIn() {
        googleLoginRepository.signInWithGoogle()
    }

}