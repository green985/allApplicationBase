package com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class UserProfileUiState(
    val username: String = "",
    val biographyText: String = "",
    val userImageList: PersistentList<FirebaseUserImageModel> = persistentListOf(),
)

data class FirebaseUserImageModel(
    val imageUrl: String = "",
    val imageId: String = "",
)

sealed class UserProfileUiEvent {
    data class OnBiographyTextChange(val newText: String) : UserProfileUiEvent()
    data class OnMessageUserClick(val receiverUserId: String) : UserProfileUiEvent()
    data class OnImageClick(val imageModel: FirebaseUserImageModel) : UserProfileUiEvent()
}
