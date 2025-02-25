package com.oyetech.composebase.sharedScreens.messaging

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.base.baseList.ListUIEvent
import com.oyetech.composebase.base.baseList.LoadableLazyColumn
import com.oyetech.composebase.base.baseList.LoadableLazyColumnState
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
Created by Erdi Özbek
-16.02.2025-
-19:00-
 **/

@Suppress("FunctionName")
@Composable
fun MessageDetailScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    conversationId: String,
    receiverUserId: String,
) {
    val vm = koinViewModel<MessageDetailVm> {
        parametersOf(
            conversationId, receiverUserId
        )
    }

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val complexItemViewState by vm.complexItemViewState.collectAsStateWithLifecycle()

    val lazyListState = rememberLoadableLazyColumnState(
        onLoadMore = {
            vm.handleListEvent(ListUIEvent.LoadMore)
        },
    )

    MessageDetailScreen(
        messageText = uiState.messageText,
        onMessageTextChanged = { vm.onEvent(MessageDetailEvent.OnMessageTextChange(it)) },
        onMessageSend = { vm.onEvent(MessageDetailEvent.OnMessageSend) },
        lazyListState = lazyListState,
        complexItemViewState = complexItemViewState,
        onRetry = { vm.onEvent(MessageDetailEvent.OnRetry) },
        onRefresh = { vm.onEvent(MessageDetailEvent.OnRefresh) },
    )


}

@Suppress("FunctionName", "LongParameterList")
@Composable
fun MessageDetailScreen(
    modifier: Modifier = Modifier,
    onMessageTextChanged: (String) -> Unit,
    onMessageSend: () -> Unit,
    messageText: String,
    lazyListState: LoadableLazyColumnState,
    complexItemViewState: ComplexItemListState<MessageDetailUiState>,
    onRetry: () -> Unit = {},
    onRefresh: () -> Unit = {},

    ) {
    val items = complexItemViewState.items
    BaseScaffold {
        Column(modifier = Modifier.padding(it)) {
            LoadableLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                isRefreshing = false,
                isLoadingInitial = complexItemViewState.isLoadingInitial,
                isErrorInitial = complexItemViewState.isErrorInitial,
                onRetry = onRetry,
                isEmptyList = complexItemViewState.isEmptyList,
                onRefresh = onRefresh,
                errorMessage = complexItemViewState.errorMessage,
                content = {
                    items(items = items, key = { it.messageId }, itemContent = { messageDetail ->
                        Column {

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                text = messageDetail.toString()
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = messageDetail.status.toString()
                            )
                        }
                    })

                },
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp)
                    .weight(1f)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = messageText,
                    onValueChange = { onMessageTextChanged(it) },
                    shape = RoundedCornerShape(RadioDimensions.inputFieldRadius),
                    label = { Text(LanguageKey.commentInputAreaHint) },
                    trailingIcon = {
                        IconButton(enabled = messageText.isNotBlank(), onClick = {
                            onMessageSend()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Localized description",
                            )
                        }
                    }
                )
            }
            Spacer(Modifier.size(16.dp))

        }
    }


}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MessageDetailPreview() {
    /*MessageDetailScreen(
        onMessageTextChanged = {},
        onMessageSend = {},
        messageText = "Hello",
        lazyPagingItems = flowOf(
            PagingData.from(
                listOf(
                    MessageDetailUiState(
                        messageId = "1",
                        messageText = "Hello",
                    ),
                    MessageDetailUiState(
                        messageId = "1",
                        messageText = "Hello",
                    ),
                    MessageDetailUiState(
                        messageId = "1",
                        messageText = "Hello",
                    ),
                    MessageDetailUiState(
                        messageId = "1",
                        messageText = "Hello",
                    ),
                )
            )
        ).collectAsLazyPagingItems(),
        lazyListState = lazyListState,
        complexItemViewState = complexItemViewState

    )*/

}