package com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.authOperation.AuthOperationVM
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.sharedScreens.navigation.ScreenKey
import com.oyetech.domain.repository.firebase.FirebaseUserPropertyRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.questionProject.questionOperation.QuestionListType
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for User2ProfileScreenSetup
 * Handles user profile display, question list types, and login state management
 *
 * Created by Warp Agent
 * -6.06.2025-
 * -22:28-
 */
class UserProfileVm2(
    appDispatchers: AppDispatchers,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val authOperationVM: AuthOperationVM,
    private val firebaseUserPropertyRepository: FirebaseUserPropertyRepository,
    private val navigationUseCase: NavigationUseCase,
    private val receiverId: String = "",
) : BaseViewModel(appDispatchers) {

    private val _uiState = MutableStateFlow(UserProfileUiState2())
    val uiState: StateFlow<UserProfileUiState2> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(getDispatcherIo()) {
            initializeProfile()
        }

        viewModelScope.launch(getDispatcherIo()) {
            firebaseUserPropertyRepository.updateOperationSharedEvent.collectLatest {
                Timber.d("Received update operation event: $it")
                firebaseUserRepository.getUserProfileWithUserId(uiState.value.userId).collect({
                    Timber.d("Refreshing user profile data due to update event.")
                })
            }
        }
    }

    private suspend fun initializeProfile() {
        val isFromTab = receiverId.isEmpty()
        _uiState.updateState {
            copy(
                isFromTab = isFromTab,
                questionListTypes = buildQuestionListTypes(),
                currentQuestionListType = QuestionListType.USERS_ANSWERS
            )
        }

        if (receiverId.isNotEmpty()) {
            loadUserProfile(receiverId)
        } else {
            loadCurrentUserProfile()
        }
    }

    private fun buildQuestionListTypes(): ImmutableList<QuestionListTypeItem> {
        return listOf(
            QuestionListTypeItem(
                type = QuestionListType.USERS_ANSWERS,
                title = LanguageKey.usersAnswers
            ),
            QuestionListTypeItem(
                type = QuestionListType.USERS_QUESTIONS,
                title = LanguageKey.usersQuestions
            )
        ).toImmutableList()
    }

    private suspend fun loadUserProfile(userId: String) {
        _uiState.updateState { copy(isLoading = true, userId = userId) }

        firebaseUserRepository.getUserProfileWithUserId(userId).asResult().collect { result ->
            result.fold(
                onSuccess = { user ->
                    val currentUserId = authOperationVM.getUserId()
                    val isOwnProfile = currentUserId == userId
                    _uiState.updateState {
                        copy(
                            isLoading = false,
                            username = user.username,
                            isNotLogin = false,
                            biographyText = user.biography,
                            userId = userId,
                            isOwnProfile = isOwnProfile
                        )
                    }
                },
                onFailure = { error ->
                    Timber.e("Error loading user profile: $error")
                    _uiState.updateState {
                        copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = "Failed to load user profile",
                            userId = userId
                        )
                    }
                }
            )
        }
    }

    private suspend fun loadCurrentUserProfile() {
        _uiState.updateState { copy(isLoading = true) }


        authOperationVM.authOperationState.collectLatest {
            Timber.d("Auth operation state changed: $it")
            val isLoggedIn = it.isLogin
            val currentUserId = it.userId

            if (currentUserId.isBlank()) {
                _uiState.updateState {
                    copy(
                        isLoading = false,
                        isNotLogin = true,
                        userId = ""
                    )
                }
            } else {
                val username = it.username ?: ""
                val biography = it.biography ?: ""
                val isOwnProfile = true // Since this is the current user's profile
                _uiState.updateState {
                    copy(
                        isLoading = false,
                        isError = false,
                        userId = currentUserId,
                        isNotLogin = !isLoggedIn,
                        username = username,
                        biographyText = biography,
                        isOwnProfile = isOwnProfile,
                    )
                }
            }
        }
    }

    override fun onEvent(event: Any) {
        when (event) {
            is UserProfileUiEvent2.OnQuestionListTypeChanged -> {
                _uiState.updateState {
                    copy(currentQuestionListType = event.type)
                }
            }

            is UserProfileUiEvent2.OnLoginButtonClicked -> {
                // todo will be fixed.
                navigateToLogin()
            }

            is UserProfileUiEvent2.OnMessageUserClick -> {
                navigateToMessage(event.receiverUserId)
            }

//            is UserProfileUiEvent2.OnImageClick -> {
//                 Handle image click if needed
//            }

            is UserProfileUiEvent2.OnBiographyTextChange -> {
                _uiState.updateState {
                    copy(biographyText = event.newText)
                }
            }

            is UserProfileUiEvent2.OnEditProfileClick -> {
                navigateToEditProfile()
            }
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            navigationUseCase.navigateTo(QuestionAppProjectRoutes.CompleteProfileScreen.route)
        }
    }

    private fun navigateToMessage(receiverUserId: String) {
        viewModelScope.launch {
            val route =
                "${QuestionAppProjectRoutes.MessageDetail.route}?${ScreenKey.receiverUserId}=$receiverUserId"
            navigationUseCase.navigateTo(route)
        }
    }

    private fun navigateToEditProfile() {
        viewModelScope.launch {
            navigationUseCase.navigateTo(QuestionAppProjectRoutes.EditProfile.route)
        }
    }
}
