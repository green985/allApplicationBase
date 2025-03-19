package com.oyetech.composebase.experimental.viewModelSlice

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-24.12.2024-
-19:39-
 **/

class UserOperationViewModelSlice(
    private val profileRepository: FirebaseUserRepository,
    private val snackbarDelegate: SnackbarDelegate,
) {

    val userPropertyState = MutableStateFlow(FirebaseUserProfileModel())

    init {
        observeUserState()
    }

    context(BaseViewModel)
    fun deleteUser(uid: String = "") {
        val userId = userPropertyState.value.userId
        profileRepository.deleteUser(userId)
    }

    private fun observeUserState() {
        GlobalScope.launch(Dispatchers.IO) {
            profileRepository.userDataStateFlow.asResult().onEach {
                delay(1000)
                Timber.d(" Google User State Flow: $it")
                it.fold(
                    onSuccess = { userData ->
                        if (userData != null) {
                            userPropertyState.value = userData
                        }

                        if (userData?.isUserDeleted == true) {
                            snackbarDelegate.triggerSnackbarState(LanguageKey.deleteAccountSuccess)
                        }
                    },
                    onFailure = {
                        Timber.d(" Google User State Flow Error: $it")
                    }
                )
            }.collect()
        }
    }


}