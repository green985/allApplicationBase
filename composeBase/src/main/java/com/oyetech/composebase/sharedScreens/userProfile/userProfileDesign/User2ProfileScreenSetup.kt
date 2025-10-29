package com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.dotIndicator.DotsIndicatorSmallAnim
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListWithParamsScreenSetup
import com.oyetech.composebase.sharedScreens.userProfile.views.ProfileBiographyInputArea
import com.oyetech.composebase.sharedViews.app.ApplicationLogoPlaceholder
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.contextHelper.getApplicationLogo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
Created by Erdi Özbek
-6.06.2025-
-22:28-
 **/

private const val BoxHeightPercent = 0.4f

@Composable
fun User2ProfileScreenSetup(
    modifier: Modifier = Modifier,
    receiverUserId: String = "",
) {
    val viewModel = koinViewModel<UserProfileVm2> {
        parametersOf(
            receiverUserId
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    val onEvent: (UserProfileUiEvent2) -> Unit = { event ->
        viewModel.onEvent(event)
    }

    User2ProfileScreen(
        modifier = modifier,
        uiState = uiState,
        receiverUserId = receiverUserId,
        onEvent = onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun User2ProfileScreen(
    modifier: Modifier = Modifier,
    receiverUserId: String,
    uiState: UserProfileUiState2 = UserProfileUiState2(),
    onEvent: (UserProfileUiEvent2) -> Unit = { },
) {
    BaseScaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = if (uiState.isNotLogin) LanguageKey.userProfile else uiState.username,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        })
    }) { contentPadding ->
        Column(
            Modifier
                .padding(contentPadding)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingScreenFullSize()
                }

                uiState.isNotLogin -> {
                    LoginRequiredContent(
                        modifier = Modifier.fillMaxSize(),
                        onLoginClick = { onEvent(UserProfileUiEvent2.OnLoginButtonClicked) }
                    )
                }

                uiState.isError -> {
                    ErrorScreenFullSize(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = uiState.errorMessage
                    )
                }

                else -> {
                    ProfileContent(
                        modifier = Modifier.fillMaxSize(),
                        uiState = uiState,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginRequiredContent(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = LanguageKey.loginToViewProfile,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLoginClick,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = LanguageKey.login)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    uiState: UserProfileUiState2,
    onEvent: (UserProfileUiEvent2) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = uiState.questionListTypes.indexOf(
            uiState.questionListTypes.find { it.type == uiState.currentQuestionListType }
        ).coerceAtLeast(0),
        pageCount = { uiState.questionListTypes.size }
    )
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    Column(modifier = modifier) {
        // Biography
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileBiographyInputArea(
                isEditMode = false,
                biographyText = uiState.biographyText,
                onBiographyTextChange = {
                    onEvent(UserProfileUiEvent2.OnBiographyTextChange(it))
                }
            )
        }

        // Question List Type Selector with HorizontalPager
        if (uiState.questionListTypes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                uiState.questionListTypes.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                            onEvent(UserProfileUiEvent2.OnQuestionListTypeChanged(item.type))
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = uiState.questionListTypes.size
                        ), icon = {}
                    ) {
                        Text(text = item.title)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // HorizontalPager for content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val listType = uiState.questionListTypes[page].type
                if (pagerState.settledPage == page) {
                    QuestionListWithParamsScreenSetup(
                        questionListType = listType.name,
                        userId = uiState.userId
                    )
                } else {
                    ApplicationLogoPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun QuestionListTypeSelector(
    questionListTypes: ImmutableList<QuestionListTypeItem>,
    currentType: QuestionListType,
    onTypeChanged: (QuestionListType) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        questionListTypes.forEach { item ->
            SegmentedButton(
                selected = currentType == item.type,
                onClick = { onTypeChanged(item.type) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = questionListTypes.indexOf(item),
                    count = questionListTypes.size
                ), icon = {}
            ) {
                Text(text = item.title)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun UserImageListView(
    modifier: Modifier = Modifier,
    imageList: PersistentList<FirebaseUserImageModel> = persistentListOf(),
    onImageClick: (() -> Unit),
) {
    val pagerState = rememberPagerState { imageList.size }
    val context = LocalContext.current
    val boxModifier = modifier
        .fillMaxWidth()
        .fillMaxHeight(BoxHeightPercent)
    Box {
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
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable { onImageClick() },
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
            DotsIndicatorSmallAnim(
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
        uiState = getDefaultUiState2()
    )
}

private fun getDefaultUiState2() = UserProfileUiState2(
    username = "Erdi Özbek",
    biographyText = "This is a sample biography text for preview purposes.",
    currentQuestionListType = QuestionListType.USERS_ANSWERS,
    questionListTypes = persistentListOf(
        QuestionListTypeItem(
            type = QuestionListType.USERS_ANSWERS,
            title = LanguageKey.usersAnswers
        ),
        QuestionListTypeItem(
            type = QuestionListType.USERS_QUESTIONS,
            title = LanguageKey.usersQuestions
        )
    ),
)
