package com.oyetech.composebase.sharedScreens.userProfile

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign.FirebaseUserImageModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-21.04.2025-
-14:36-
 **/

data class UserProfileUiState(
    val username: String = "",
    val userCreatedTimeString: String = "",
    val biographyText: String = "",
    val isOwner: Boolean = false,// detect user click with tab
) : BaseUIState()

sealed class UserProfileEvent : BaseEvent() {
    object OnEditProfile : UserProfileEvent()

}

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val biographyText: String = "",
    val imageList: PersistentList<FirebaseUserImageModel> = persistentListOf(),
)

sealed class EditProfileEvent {
    data class OnBiographyTextChange(val biographyText: String) : EditProfileEvent()
    data class OnImageSlotClick(val index: Int) : EditProfileEvent()
    data class OnImageSelected(val imageUri: String) : EditProfileEvent()
    object OnCancelOperation : EditProfileEvent()
    object OnSubmit : EditProfileEvent()
}