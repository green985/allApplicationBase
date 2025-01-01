package com.oyetech.composebase.experimental.commentWidget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationScreenSetup
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
import timber.log.Timber

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
    val loginOperationState by loginOperationVM.loginOperationState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }


    Timber.d("CommentScreenWithContentScreenSetup == " + snackbarHostState.currentSnackbarData.toString())


    BaseScaffold() {
        Column(modifier = Modifier.padding(it)) {
            LoginOperationScreenSetup(navigationRoute = navigationRoute)

            val lazyPagingItems = vm.commentPageState.collectAsLazyPagingItems()

            BasePagingListScreen(
                reverseLayout = true,
                modifier = Modifier.weight(1f),
                items = lazyPagingItems, // This parameter is abstracted, not used here
                itemKey = { item -> item.createdAt.time },
                onBindItem = { item ->
                    CommentItemView(uiState = item, onEvent = { event ->
                        vm.onEvent(event)
                    })
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
    }
    when (uiState.addCommentState) {
        is Error -> {
            val errorMessage = (uiState.addCommentState as Error).exception.message ?: ""
//            ErrorScreenFullSize(errorMessage)
            snackbarDelegate.triggerSnackbarState(
                message = errorMessage,
            )
        }

        Loading -> {
            LoadingScreenFullSize()
        }

        is Success -> {
            LaunchedEffect(uiState.addCommentState) {

                snackbarDelegate.triggerSnackbarState(
                    message = LanguageKey.commentAddedSuccessfully,
                    actionLabel = "deneme action label",
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
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
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

