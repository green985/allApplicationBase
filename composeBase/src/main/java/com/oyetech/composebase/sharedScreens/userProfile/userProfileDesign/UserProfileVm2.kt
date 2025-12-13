package com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.sharedScreens.navigation.ScreenKey
import com.oyetech.domain.repository.firebase.FirebaseUserPropertyRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
                    val currentUserId = firebaseUserRepository.getUserId()
                    val isOwnProfile = currentUserId == userId
                    _uiState.updateState {
                        copy(
                            isLoading = false,
                            username = user.username ?: "",
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

        firebaseUserRepository.userProfileDataStateFlow.asResult().collectLatest { result ->
            result.fold(
                onSuccess = { user ->
                    if (user != null) {
                        val currentUserId = firebaseUserRepository.getUserId()
                        _uiState.updateState {
                            copy(
                                isLoading = false,
                                username = user.username ?: "",
                                isNotLogin = false,
                                biographyText = user.biography,
                                userId = currentUserId,
                                isOwnProfile = true
                            )
                        }
                    } else {
                        _uiState.updateState {
                            copy(
                                isLoading = false,
                                isNotLogin = true
                            )
                        }
                    }
                },
                onFailure = { error ->
                    Timber.e("Error loading current user: $error")
                    _uiState.updateState {
                        copy(
                            isLoading = false,
                            isNotLogin = true
                        )
                    }
                }
            )
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

            is UserProfileUiEvent2.OnImageClick -> {
                // Handle image click if needed
            }

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

/**
 * UI State for User2ProfileScreenSetup
 */
data class UserProfileUiState2(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isNotLogin: Boolean = false,
    val username: String = "",
    val biographyText: String = "",
    val userImageList: ImmutableList<FirebaseUserImageModel> = persistentListOf(),
    val questionListTypes: ImmutableList<QuestionListTypeItem> = persistentListOf(),
    val currentQuestionListType: QuestionListType = QuestionListType.USERS_ANSWERS,
    val userId: String = "",
    val isOwnProfile: Boolean = false,
    val isFromTab: Boolean = false,
)

/**
 * Question list type enumeration
 */
enum class QuestionListType {
    USERS_ANSWERS,
    USERS_QUESTIONS
}

/**
 * Question list type item for UI display
 */
data class QuestionListTypeItem(
    val type: QuestionListType,
    val title: String,
)

/**
 * UI Events for User2ProfileScreenSetup
 */
sealed class UserProfileUiEvent2 {
    data class OnQuestionListTypeChanged(val type: QuestionListType) : UserProfileUiEvent2()
    data object OnLoginButtonClicked : UserProfileUiEvent2()
    data class OnMessageUserClick(val receiverUserId: String) : UserProfileUiEvent2()
    data class OnImageClick(val imageModel: FirebaseUserImageModel) : UserProfileUiEvent2()
    data class OnBiographyTextChange(val newText: String) : UserProfileUiEvent2()
    data object OnEditProfileClick : UserProfileUiEvent2()
}
