package com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
//    val userImageList: ImmutableList<FirebaseUserImageModel> = persistentListOf(),
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
//    data class OnImageClick(val imageModel: FirebaseUserImageModel) : UserProfileUiEvent2()
    data class OnBiographyTextChange(val newText: String) : UserProfileUiEvent2()
    data object OnEditProfileClick : UserProfileUiEvent2()
}
