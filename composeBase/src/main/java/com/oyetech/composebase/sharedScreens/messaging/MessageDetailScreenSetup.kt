package com.oyetech.composebase.sharedScreens.messaging

import MessageDetailItemView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.GenericListScreenSetup
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseList.LoadableLazyColumnState
import com.oyetech.composebase.base.baseList.rememberLoadableLazyColumnState
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.languageModule.keyset.LanguageKey
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

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
    val listViewState by vm.listViewState.collectAsStateWithLifecycle()

    val lazyColumnState: LoadableLazyColumnState =
        rememberLoadableLazyColumnState(onLoadMore = { listViewState.triggerLoadMore?.invoke() })

    MessageDetailScreen(
        onMessageTextChanged = { vm.onEvent(MessageDetailEvent.OnMessageTextChange(it)) },
        onMessageSend = {
            vm.onEvent(MessageDetailEvent.OnMessageSend(true))
        },
        uiState = uiState,
        listViewState = listViewState,
        lazyColumnState = lazyColumnState
    )

    if (uiState.onMessageSendTriggered) {
        LaunchedEffect(System.currentTimeMillis()) {
            lazyColumnState.lazyListState.animateScrollToItem(0)
            vm.onEvent(MessageDetailEvent.OnMessageSend(false))
        }
    }

}

@Suppress("FunctionName", "LongParameterList")
@Composable
fun MessageDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MessageDetailScreenUiState,
    onMessageTextChanged: (String) -> Unit,
    onMessageSend: () -> Unit,
    listViewState: GenericListState<MessageDetailUiState>,
    lazyColumnState: LoadableLazyColumnState = rememberLoadableLazyColumnState(onLoadMore = { listViewState.triggerLoadMore?.invoke() }),
) {
    val items = listViewState.items
    BaseScaffold { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Box(modifier = Modifier.weight(1f)) {
                GenericListScreenSetup(
                    modifier = Modifier
                        .fillMaxSize(),
                    listViewState = listViewState,
                    lazyColumnState = lazyColumnState,
                    reverseLayout = true,
                    content = {
                        itemsIndexed(
                            items = items,
                            key = { index, it -> it.messageId },
                            itemContent = { index, messageDetail ->
                                MessageDetailItemView(
                                    modifier = Modifier.clickable {
                                        Timber.d("MessageDetailItemView== $messageDetail")
                                        Timber.d("MessageDetailItemView== $index")
                                    },
                                    uiState = messageDetail,
                                    currentUserId = uiState.currentUserId
                                )
                            })
                    },
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp)
                    .consumeWindowInsets(contentPadding)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.messageText,
                    onValueChange = { onMessageTextChanged(it) },
                    shape = RoundedCornerShape(RadioDimensions.inputFieldRadius),
                    label = { Text(LanguageKey.commentInputAreaHint) },
                    trailingIcon = {
                        IconButton(enabled = uiState.messageText.isNotBlank(), onClick = {
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
        }
    }


}

@Preview(showBackground = true)
@Composable
private fun MessageDetailPreview() {

    MessageDetailScreen(
        uiState = MessageDetailScreenUiState(
            isLoading = false,
            errorText = "placerat",
            messageText = "pertinax",
            createdAt = null,
            createdAtString = "porta",
            senderId = "sumo",
            receiverId = "idque",
            currentUserId = "postulant",
            conversationId = "cursus"
        ),
        onMessageTextChanged = {}, onMessageSend = {},
        listViewState = GenericListState(
            items = persistentListOf(
                MessageDetailUiState(
                    messageId = "mollis",
                    content = "asdasdasdasdadwwww",
                    createdAt = null,
                    createdAtString = "porta",
                    senderId = "sumo",
                    receiverId = "idque",
                    conversationId = "cursus"
                )
            )
        ),
    )


}