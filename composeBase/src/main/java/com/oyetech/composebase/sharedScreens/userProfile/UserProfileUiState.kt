package com.oyetech.composebase.sharedScreens.userProfile

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState

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
    data class Idle(val data: Int) : UserProfileEvent()
    object Idlee : UserProfileEvent()
    object OnEditProfile : UserProfileEvent()

}

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val biographyText: String = "",
)

sealed class EditProfileEvent {
    data class Idle(val data: Int) : EditProfileEvent()
    object Idlee : EditProfileEvent()
    data class OnBiographyTextChange(val biographyText: String) : EditProfileEvent()
    object OnCancelOperation : EditProfileEvent()
    object OnSubmit : EditProfileEvent()
}