package com.oyetech.composebase.sharedScreens.userProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseScaffoldDeprecated
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarActionItems
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiograpyhyInputArea
import com.oyetech.languageModule.keyset.LanguageKey
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
    val vm = koinViewModel<UserProfileVm> {
        parametersOf(
            receiverUserId
        )
    }
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    UserProfileScreen(
        uiState = uiState,
        navigationRoute = navigationRoute,
        onEvent = { vm.onEvent(it) }
    )
}

@Composable
fun UserProfileScreen2(
    uiState: UserProfileUiState,
    onEditClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EDF2))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.username,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )
            }
        }

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Name
        Text(
            text = uiState.username,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Biography Hint (non-editable in this case)
        OutlinedTextField(
            value = uiState.biographyText ?: "",
            onValueChange = {},
            label = { Text("Biography") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Created At
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile Created At",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = uiState.userCreatedTimeString,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Suppress("FunctionName")
@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    uiState: UserProfileUiState,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    onEvent: (UserProfileEvent) -> (Unit),
) {
    BaseScaffoldDeprecated(showTopBar = true, topBarContent = {
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
            Column(modifier = Modifier.padding(16.dp)) {

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
                    Text(
                        text = LanguageKey.profileCreatedAt,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = uiState.userCreatedTimeString,
                        style = MaterialTheme.typography.bodyMedium
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

@Preview(showBackground = true)
@Composable
private fun UserProfileScreenPreview2() {
    UserProfileScreen2(uiState = UserProfileUiState(
        username = "Harriett Owens",
        userCreatedTimeString = "2025-03-20",
        isOwner = false
    ), {})
}