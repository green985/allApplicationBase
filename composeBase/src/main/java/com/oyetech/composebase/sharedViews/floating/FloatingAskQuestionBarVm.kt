package com.oyetech.composebase.sharedViews.floating

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for managing FloatingAskQuestionBar state and business logic
 *
 * Handles:
 * - Observing user login status
 * - Managing FAB visibility
 * - Navigation to create question screen
 */
class FloatingAskQuestionBarVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val firebaseUserRepository: FirebaseUserRepository,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(FloatingAskQuestionBarUiState())

    init {
        observeLoginStatus()
    }

    override fun onEvent(event: Any) {
        if (event is FloatingAskQuestionBarEvent) {
            when (event) {
                FloatingAskQuestionBarEvent.OnFabClicked -> navigateToCreateQuestion()
            }
        }
    }

    private fun observeLoginStatus() {
        viewModelScope.launch(getDispatcherIo()) {
            firebaseUserRepository.userProfileDataStateFlow.asResult().collectLatest { result ->
                result.fold(
                    onSuccess = { userData ->
                        val isVisible = userData.isProfileComplete()
                        uiState.value = uiState.value.copy(isVisible = isVisible)
                        Timber.d("FloatingAskQuestionBar visibility: $isVisible")
                    },
                    onFailure = { exception ->
                        uiState.value = uiState.value.copy(isVisible = false)
                        Timber.e("Failed to observe login status: ${exception.message}")
                    }
                )
            }
        }
    }

    private fun navigateToCreateQuestion() {
        navigationUseCase.navigateTo(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
    }
}
