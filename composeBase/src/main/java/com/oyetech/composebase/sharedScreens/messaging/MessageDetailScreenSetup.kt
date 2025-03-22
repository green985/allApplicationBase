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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListScreenSetup2
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.LoadableLazyColumnState
import com.oyetech.composebase.base.baseGenericList.rememberLoadableLazyColumnState
import com.oyetech.composebase.base.baseGenericList.safeScrollToItem
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailUiEvent.OnMessageIdle
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailUiEvent.OnMessageSend
import com.oyetech.languageModule.keyset.LanguageKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
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
    val coroutineScope = rememberCoroutineScope()
    val listViewState by vm.listViewState.collectAsStateWithLifecycle()

    val lazyColumnState: LoadableLazyColumnState =
        rememberLoadableLazyColumnState(onLoadMore = { listViewState.triggerLoadMore?.invoke() })

    val lazyColumnState2: LoadableLazyColumnState =
        rememberLoadableLazyColumnState(onLoadMore = { listViewState.triggerLoadMore?.invoke() })

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    MessageDetailScreen(
        onMessageTextChanged = { vm.onEvent(MessageDetailEvent.OnMessageTextChange(it)) },
        onMessageSend = {
            vm.onEvent(MessageDetailEvent.OnMessageSend(true))
        },
        uiState = uiState,
        lazyColumnState = lazyColumnState,
        listViewState = listViewState,
        baseListViewModel = vm,
    )

    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { event ->
            when (event) {
                OnMessageIdle -> {
                    Timber.d("MessageDetailUiEvent.OnMessageSendSuccess " + event.toString())
                }

                is OnMessageSend -> {
                    Timber.d("MessageDetailUiEvent.OnMessageSendSuccess " + event.toString())
                    delay(200)
                    safeScrollToItem(lazyColumnState2.lazyListState, 0)
                }
            }
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
    baseListViewModel: BaseListViewModel<MessageDetailUiState>,
    lazyColumnState: LoadableLazyColumnState,
) {
    val items = listViewState.items
    BaseScaffold { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Box(modifier = Modifier.weight(1f)) {
                GenericListScreenSetup2(
                    lazyColumnState = lazyColumnState,
                    modifier = Modifier
                        .fillMaxSize(),
                    reverseLayout = true,
                    viewModel = baseListViewModel,
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


}