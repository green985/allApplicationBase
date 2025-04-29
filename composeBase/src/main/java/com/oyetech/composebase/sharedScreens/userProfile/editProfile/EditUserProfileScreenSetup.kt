package com.oyetech.composebase.sharedScreens.userProfile.editProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.customViews.FormAcceptOperationViewSetup
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileEvent
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileUiState
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiograpyhyInputArea
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-21.04.2025-
-14:36-
 **/

@Suppress("FunctionName")
@Composable
fun EditProfileScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<EditProfileVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    BaseScaffold {
        Column(modifier = Modifier.padding()) {

        }
    }

}

@Suppress("FunctionName")
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    uiState: EditProfileUiState,
    onEvent: (EditProfileEvent) -> (Unit),
) {
    BaseScaffold(topBarContent = {
        FormAcceptOperationViewSetup(
            onCancelOperation = {
                onEvent(EditProfileEvent.OnCancelOperation)
            }, onAcceptOperation = { onEvent(EditProfileEvent.OnSubmit) })
    }) {
        Column(modifier = Modifier.padding()) {
            ProfileBiograpyhyInputArea(
                isEditMode = true,
                biographyText = uiState.biographyText,
                onBiographyTextChange = { onEvent(EditProfileEvent.OnBiographyTextChange(it)) }
            )
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun EditProfilePreview() {
    EditProfileScreen(
        uiState = EditProfileUiState(),
        onEvent = {}
    )
}