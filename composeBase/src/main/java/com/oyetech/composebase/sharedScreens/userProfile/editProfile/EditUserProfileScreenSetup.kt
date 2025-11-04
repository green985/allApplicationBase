package com.oyetech.composebase.sharedScreens.userProfile.editProfile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.customViews.FormAcceptOperationViewSetup
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileEvent
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiographyInputArea
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-8.06.2025-
-18:49-
 **/

@Composable
fun EditUserProfileScreenSetup(
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<EditProfileVm>()
    val uiState by viewModel.uiState.collectAsState()
    val onEvent: (EditProfileEvent) -> Unit = { event ->
        viewModel.onEvent(event)
    }

    EditUserProfileScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = onEvent
    )
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditUserProfileScreen(
    modifier: Modifier = Modifier,
    uiState: EditProfileUiState = EditProfileUiState(),
    onEvent: (EditProfileEvent) -> Unit = { },
) {
    BaseScaffold(
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    ProfileBiographyInputArea(
                        isEditMode = true,
                        biographyText = uiState.biographyText,
                        onBiographyTextChange = { onEvent(EditProfileEvent.OnBiographyTextChange(it)) }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                if (uiState.isLoading) {
                    LoadingScreenFullSize()
                }

                if (uiState.errorMessage.isNotEmpty()) {
                    ErrorScreenFullSize(
                        modifier = Modifier.align(Alignment.Center),
                        errorMessage = uiState.errorMessage
                    )
                }
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
