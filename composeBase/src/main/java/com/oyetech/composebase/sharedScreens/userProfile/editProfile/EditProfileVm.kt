package com.oyetech.composebase.sharedScreens.userProfile.editProfile

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileEvent
import com.oyetech.domain.repository.firebase.FirebaseUserPropertyRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class EditProfileVm(
    appDispatchers: AppDispatchers,
    private val firebaseUserPropertyRepository: FirebaseUserPropertyRepository,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val navigationUseCase: NavigationUseCase,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(EditProfileUiState())

    init {
        loadCurrentBiography()
    }

    private fun loadCurrentBiography() {
        viewModelScope.launch(getDispatcherIo()) {
            val currentUser = firebaseUserRepository.userDataStateFlow.value
            uiState.updateState {
                copy(biographyText = currentUser.biography)
            }
        }
    }

    override fun onEvent(event: Any) {
        when (event) {
            is EditProfileEvent.OnBiographyTextChange -> {
                uiState.updateState {
                    copy(biographyText = event.newText)
                }
            }

            is EditProfileEvent.OnSubmit -> {
                submitBiography()
            }

            is EditProfileEvent.OnCancelOperation -> {
                navigateBack()
            }

            else -> {}
        }
    }

    private fun submitBiography() {
        viewModelScope.launch(getDispatcherIo()) {
            uiState.updateState { copy(isLoading = true, errorMessage = "") }

            val userId = firebaseUserRepository.getUserId()
            val biography = uiState.value.biographyText

            firebaseUserPropertyRepository.updateBiography(userId, biography)
                .onSuccess {
                    uiState.updateState { copy(isLoading = false) }
                    Timber.d("Biography updated successfully")
                    firebaseUserPropertyRepository.updateOperationSharedEvent.emit(Unit)
                    navigateBack()
                }
                .onFailure { error ->
                    uiState.updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: LanguageKey.editProfileError
                        )
                    }
                    Timber.e("Biography update failed: ${error.message}")
                }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            navigationUseCase.navigateTo("back")
        }
    }
}
