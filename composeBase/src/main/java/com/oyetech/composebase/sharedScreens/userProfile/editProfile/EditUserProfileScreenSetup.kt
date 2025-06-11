package com.oyetech.composebase.sharedScreens.userProfile.editProfile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.oyetech.composebase.baseViews.customViews.FormAcceptOperationViewSetup
import com.oyetech.composebase.helpers.viewProperties.ImagePickerHandler
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileEvent
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileEvent.OnImageSlotClick
import com.oyetech.composebase.sharedScreens.userProfile.EditProfileUiState
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiograpyhyInputArea
import com.oyetech.domain.repository.firebase.FirebaseStorageRepository
import org.koin.compose.koinInject
import timber.log.Timber

/**
Created by Erdi Özbek
-8.06.2025-
-18:49-
 **/

@Composable
fun EditUserProfileScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    EditUserProfileScreen(
        modifier = modifier,
        navigationRoute = navigationRoute,
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
    navigationRoute: (navigationRoute: String) -> Unit = {},
    uiState: EditProfileUiState = getDefaultUiState(),
    onEvent: EditProfileEvent.() -> Unit = { Timber.d("${this.toString()}") }, // Default empty event handler
) {

    val firebaseStorageRepository = koinInject<FirebaseStorageRepository>()

    val launchPicker = ImagePickerHandler { uri ->
        Timber.d("Image URI: $uri")
        onEvent(EditProfileEvent.OnImageSelected(uri.toString()))
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FormAcceptOperationViewSetup(
                modifier.statusBarsPadding(),
                onCancelOperation = {
                    onEvent(EditProfileEvent.OnCancelOperation)
                }, onAcceptOperation = { onEvent(EditProfileEvent.OnSubmit) })

//            FormAcceptOperationViewSetup(
//                onCancelOperation = {
//                    onEvent(EditProfileEvent.OnCancelOperation)
//                }, onAcceptOperation = { onEvent(EditProfileEvent.OnSubmit) })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {

                // 3x2 Carousel Grid
                // Requires: accompanist-glide, androidx.compose.foundation.lazy.grid.*
                // And: androidx.compose.material.icons.Icons
                // And: androidx.compose.material.icons.filled.Add
                // Add imports if not present in your project.
                // The following code assumes imageList is List<String?> of size 6 in uiState.
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        8.dp
                    )
                ) {
                    items(6) { index ->
                        val image = uiState.imageList.getOrNull(index)
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .border(
                                    width = 1.dp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    launchPicker()
                                    onEvent(
                                        OnImageSlotClick(
                                            index
                                        )
                                    )
                                },
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            if (image != null) {
                                // Show image with GlideImage
                                // Make sure you have accompanist-glide in your project
                                GlideImage(
                                    model = image,
                                    contentDescription = "User Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                androidx.compose.material.icons.Icons
                                androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                                    contentDescription = "Add Image",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }



                Spacer(modifier = Modifier.padding(8.dp))
                ProfileBiograpyhyInputArea(
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
        navigationRoute = {}
    )
}