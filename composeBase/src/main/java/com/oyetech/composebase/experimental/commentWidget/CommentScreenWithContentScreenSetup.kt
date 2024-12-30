package com.oyetech.composebase.experimental.commentWidget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.basePagingList.BasePagingListScreen
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.experimental.commentScreen.CommentItemUiState
import com.oyetech.composebase.experimental.commentScreen.CommentScreenEvent
import com.oyetech.composebase.experimental.commentScreen.CommentScreenUiState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationScreenSetup
import com.oyetech.composebase.experimental.loginOperations.LoginOperationUiState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.newPackages.helpers.OperationState.Error
import com.oyetech.models.newPackages.helpers.OperationState.Loading
import com.oyetech.models.newPackages.helpers.OperationState.Success
import org.koin.androidx.compose.koinViewModel
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
    val loginOperationVM = koinViewModel<LoginOperationVM>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val loginOperationState by loginOperationVM.loginOperationState.collectAsStateWithLifecycle()

    BaseScaffold {
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
            ErrorScreenFullSize(errorMessage)
        }

        Loading -> {
            LoadingScreenFullSize()
        }

        is Success -> {
            // todo will be add snacbar but now refresh...
            LaunchedEffect(Unit) {
                Timber.d("Successsssss")

            }
        }

        else -> {

        }
    }

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
            .padding(start = 8.dp, end = 8.dp)
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
                label = { Text("Name") },
                modifier = Modifier.weight(1f)
            )
            IconButton(enabled = uiState.commentInput.isNotBlank(), onClick = {
                onEvent(CommentScreenEvent.OnCommentSubmit)

            }) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Localized description",
                )
            }
        }

    }
}

@Composable
fun CommentItemView(uiState: CommentItemUiState, onEvent: (CommentScreenEvent) -> (Unit)) {

    Card(modifier = Modifier.padding(4.dp)) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = uiState.commentContent,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = uiState.username,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = uiState.createdAtString,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }

}
