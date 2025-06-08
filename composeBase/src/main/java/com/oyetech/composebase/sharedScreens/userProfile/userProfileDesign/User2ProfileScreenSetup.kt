package com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideSubcomposition
import com.bumptech.glide.integration.compose.RequestState
import com.bumptech.glide.integration.compose.placeholder
import com.oyetech.composebase.baseViews.dotIndicator.DotsIndicator
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiograpyhyInputArea
import com.oyetech.tools.contextHelper.getApplicationLogo
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-6.06.2025-
-22:28-
 **/

private const val boxHeightPercent = 0.4f

@Composable
fun User2ProfileScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    User2ProfileScreen(
        modifier = modifier,
        navigationRoute = navigationRoute,
        uiState = getDefaulUiState(),
        receiverUserId = "adasd"
    )
}

data class UserProfileUiState(
    val username: String = "",
    val biographyText: String = "",
    val userImageList: PersistentList<FirebaseUserImageModel> = persistentListOf(),
)

data class FirebaseUserImageModel(
    val imageUrl: String = "",
    val imageId: String = "",
)

sealed class UserProfileUiEvent {
    data class OnBiographyTextChange(val newText: String) : UserProfileUiEvent()
    data class OnMessageUserClick(val receiverUserId: String) : UserProfileUiEvent()
    data class OnImageClick(val imageModel: FirebaseUserImageModel) : UserProfileUiEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun User2ProfileScreen(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    receiverUserId: String,
    uiState: UserProfileUiState = UserProfileUiState(),
    onEvent: UserProfileUiEvent.() -> Unit = { }, // Default empty event handler
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = uiState.username,
                modifier = Modifier.padding(start = 16.dp),
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
        })
    }) { contentPadding ->
        Column(
            Modifier
                .padding(contentPadding)
        ) {
            UserImageListView(imageList = uiState.userImageList)

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // biography or other user details
                ProfileBiograpyhyInputArea(
                    isEditMode = false,
                    biographyText = uiState.biographyText ?: "",
                    onBiographyTextChange = {
                        onEvent(UserProfileUiEvent.OnBiographyTextChange(it))
                    })
            }

            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    onEvent.invoke(UserProfileUiEvent.OnMessageUserClick(receiverUserId))
                }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Send,
                        contentDescription = "Message User",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun UserImageListView(
    modifier: Modifier = Modifier,
    imageList: PersistentList<FirebaseUserImageModel> = persistentListOf(),
) {
    val pagerState = rememberPagerState { imageList.size }
    val context = LocalContext.current
    val boxModifier = modifier
        .fillMaxWidth()
        .fillMaxHeight(boxHeightPercent)
    Box() {
        if (imageList.isEmpty()) {
            Box(
                modifier = boxModifier
                    .background(MaterialTheme.colorScheme.onError)
            )
        } else {
            Column(
                boxModifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                ) { pageIndex ->
                    if (LocalInspectionMode.current) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.onError
                                )
                        )
                    } else {
                        val imageUrl = imageList.get(pageIndex).imageUrl
                        GlideSubcomposition(imageUrl, Modifier.fillMaxSize()) {
                            // state comes from GlideSubcompositionScope
                            when (state) {
                                RequestState.Failure -> {
                                    placeholder(context.getApplicationLogo())
                                }

                                RequestState.Loading -> {
                                    Box(
                                        modifier = modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                                    }
                                }
                                // painter also comes from GlideSubcompositionScope
                                is RequestState.Success -> {
                                    Image(
                                        modifier = Modifier.fillMaxSize(),
                                        painter = painter,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            DotsIndicator(
                totalDots = imageList.size,
                selectedIndex = pagerState.currentPage,
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun User2ProfileScreenPreview() {
    User2ProfileScreen(
        receiverUserId = "erdiOzbek",
        navigationRoute = {}, uiState = getDefaulUiState()
    )
}

@Composable
private fun getDefaulUiState() = UserProfileUiState(
    username = "Erdi Özbeffffk",
    biographyText = "This is a sample biography text for preview purposes.",
    userImageList = persistentListOf(
        FirebaseUserImageModel("https://picsum.photos/2000", "image1"),
        FirebaseUserImageModel("https://picsum.photos/3000", "image2")
    )
)