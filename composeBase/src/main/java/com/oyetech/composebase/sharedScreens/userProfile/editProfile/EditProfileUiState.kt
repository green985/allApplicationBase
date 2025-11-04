package com.oyetech.composebase.sharedScreens.userProfile.editProfile

data class EditProfileUiState(
    val biographyText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)

fun getDefaultUiState() = EditProfileUiState(
    biographyText = "Sample biography text",
    isLoading = false,
    errorMessage = "",
)
