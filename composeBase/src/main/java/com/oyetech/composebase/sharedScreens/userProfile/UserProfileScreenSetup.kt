package com.oyetech.composebase.sharedScreens.userProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarActionItems
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiograpyhyInputArea
import com.oyetech.languageModule.keyset.LanguageKey
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-20.03.2025-
-21:22-
 **/

@Composable
fun UserProfileScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    receiverUserId: String,
) {


}

@Suppress("FunctionName")
@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    uiState: UserProfileUiState,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    onEvent: (UserProfileEvent) -> (Unit),
) {
    BaseScaffold(showTopBar = true, topBarContent = {
        QuoteToolbarSetup(uiState = QuoteToolbarState(
            title = uiState.username,
            showBackButton = uiState.isOwner,
            onActionButtonClick = {

            },
            actionButtonState = persistentListOf(
                QuoteToolbarActionItems.EditProfile(R.drawable.ic_edit),
            ),
        ), onEvent = {
            if (it is QuoteToolbarEvent.OnActionButtonClick) {
                when (it.actionItem) {

                    is QuoteToolbarActionItems.EditProfile -> {
                        navigationRoute.invoke(QuoteAppProjectRoutes.EditProfile.route)
                    }

                    else -> {

                    }
                }
            } else if (it is QuoteToolbarEvent.BackButtonClick) {
                navigationRoute.invoke("back")
            }
        })
    }) {
        Column(modifier = Modifier.padding(it)) {

            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = uiState.username,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.padding(8.dp))
                ProfileBiograpyhyInputArea(
                    isEditMode = false,
                    biographyText = uiState.biographyText,
                    onBiographyTextChange = { }
                )
                Spacer(modifier = Modifier.padding(8.dp))

                Row {
                    Text(text = LanguageKey.profileCreatedAt)
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = uiState.userCreatedTimeString,
                    )
                }
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
private fun UserProfileScreenPreview() {
    UserProfileScreen(uiState = UserProfileUiState(
        username = "Harriett Owens",
        userCreatedTimeString = "2025-03-20",
        isOwner = false
    ), navigationRoute = {}) { }
}