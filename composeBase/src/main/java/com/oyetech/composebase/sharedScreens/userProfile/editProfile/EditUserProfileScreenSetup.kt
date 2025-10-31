package com.oyetech.composebase.sharedScreens.userProfile.editProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.oyetech.composebase.baseViews.customViews.FormAcceptOperationViewSetup
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileEvent
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileUiState
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiographyInputArea
import timber.log.Timber

/**
Created by Erdi Özbek
-8.06.2025-
-18:49-
 **/

@Composable
fun EditUserProfileScreenSetup(
    modifier: Modifier = Modifier,
) {

    EditUserProfileScreen(
        modifier = modifier,
        uiState = getDefaultUiState(),
    )
}

fun getDefaultUiState(): EditProfileUiState {
    return EditProfileUiState() // Replace with actual default state object
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditUserProfileScreen(
    modifier: Modifier = Modifier,
    uiState: EditProfileUiState = getDefaultUiState(),
    onEvent: EditProfileEvent.() -> Unit = { Timber.d("$this") }, // Default empty event handler
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FormAcceptOperationViewSetup(
                modifier.statusBarsPadding(),
                onCancelOperation = {
                    onEvent(EditProfileEvent.OnCancelOperation)
                },
                onAcceptOperation = { onEvent(EditProfileEvent.OnSubmit) }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                ProfileBiographyInputArea(
                    isEditMode = true,
                    biographyText = uiState.biographyText,
                    onBiographyTextChange = { onEvent(EditProfileEvent.OnBiographyTextChange(it)) }
                )
                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
    )
}

@Preview()
@Composable
fun EditUserProfile2ScreenPreview() {
    EditUserProfileScreen(
        uiState = getDefaultUiState(),
        onEvent = { },
    )
}
