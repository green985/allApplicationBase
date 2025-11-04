package com.oyetech.composebase.sharedScreens.userProfile

sealed class EditProfileEvent {
    data class OnBiographyTextChange(val newText: String) : EditProfileEvent()
    data object OnSubmit : EditProfileEvent()
    data object OnCancelOperation : EditProfileEvent()
}
