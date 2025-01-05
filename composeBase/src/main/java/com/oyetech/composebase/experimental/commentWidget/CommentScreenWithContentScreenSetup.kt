package com.oyetech.composebase.experimental.commentWidget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.composebase.baseViews.loadingErrors.LoadingDialogFullScreen
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationUiState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.newPackages.helpers.OperationState.Error
import com.oyetech.models.newPackages.helpers.OperationState.Idle
import com.oyetech.models.newPackages.helpers.OperationState.Loading
import com.oyetech.models.newPackages.helpers.OperationState.Success
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
Created by Erdi Özbek
-30.12.2024-
-00:40-
 **/

@Composable
fun CommentScreenWithContentScreenSetup(
    contentId: String,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<CommentScreenWithContentIdVM>(key = contentId) {
        parametersOf(
            contentId
        )
    }
    val snackbarDelegate = koinInject<SnackbarDelegate>()
    val loginOperationVM = koinViewModel<LoginOperationVM>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val loginOperationState by loginOperationVM.getLoginOperationSharedState()
        .collectAsStateWithLifecycle(
            initialValue = LoginOperationUiState()
        )


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        val lazyPagingItems = vm.commentPageState.collectAsLazyPagingItems()

        BasePagingListScreen(
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            items = lazyPagingItems, // This parameter is abstracted, not used here
            itemKey = { item -> item.createdAt.time },
            onBindItem = { item ->
                AnimatedVisibility(
                    visible = !item.isDeleted,
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 300) // Silinme animasyon süresi
                    ) + shrinkVertically(
                        animationSpec = tween(durationMillis = 300),
                        shrinkTowards = Alignment.Top
                    )
                ) {
                    CommentItemView(uiState = item, onEvent = { event ->
                        vm.onEvent(event)
                    })
                }

            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        CommentInputView(
            uiState = uiState,
            onEvent = { event ->
                vm.onEvent(event)
            },
            userUiState = loginOperationState,
            onUserEvent = { event ->
                loginOperationVM.handleEvent(event)
            }
        )

    }
    when (uiState.addCommentState) {
        is Error -> {
            val errorMessage = (uiState.addCommentState as Error).exception.message ?: ""
            snackbarDelegate.triggerSnackbarState(
                message = errorMessage,
            )
        }

        Loading -> {
            LoadingDialogFullScreen()
        }

        is Success -> {
            LaunchedEffect(uiState.addCommentState) {
                snackbarDelegate.triggerSnackbarState(
                    message = LanguageKey.commentAddedSuccessfully,
                )
            }
        }

        else -> {

        }
    }

}

@Preview
@Composable
fun CommentInputViewPreview() {
    CommentInputView(uiState = CommentScreenUiState(
        contentId = "malesuada",
        commentInput = "no",
        addCommentState = Idle,
        isListEmpty = false,
        commentList = persistentListOf<CommentItemUiState>(),
        errorMessage = "vitae"
    ), userUiState = LoginOperationUiState(
        isLoading = false,
        isError = false,
        errorMessage = "has",
        isUsernameEmpty = false,
        isUserDeleted = false,
        displayName = "Elva Parrish",
        uid = "dicam",
        displayNameRemote = "Lucy Evans",
        photoUrl = "http://www.bing.com/search?q=sed",
        isLogin = true,
        isAnonymous = false,
        lastSignInTimestamp = null
    ), onEvent = {}, onUserEvent = {})
}

@Composable
fun CommentInputView(
    uiState: CommentScreenUiState,
    userUiState: LoginOperationUiState,
    onEvent: (CommentScreenEvent) -> (Unit),
    onUserEvent: (LoginOperationEvent) -> (Unit),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!userUiState.isLogin) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = {
                    onUserEvent(LoginOperationEvent.LoginClicked)
                }) {
                    Text(LanguageKey.login)
                }
            }
        } else {

            OutlinedTextField(
                value = uiState.commentInput,
                onValueChange = {
                    onEvent(CommentScreenEvent.OnCommentInputChanged(it))
                },
                shape = RoundedCornerShape(RadioDimensions.inputFieldRadius),
                label = { Text(LanguageKey.commentInputAreaHint) },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(enabled = uiState.commentInput.isNotBlank(), onClick = {
                        onEvent(CommentScreenEvent.OnCommentSubmit)

                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Localized description",
                        )
                    }
                }
            )

        }

    }
}

