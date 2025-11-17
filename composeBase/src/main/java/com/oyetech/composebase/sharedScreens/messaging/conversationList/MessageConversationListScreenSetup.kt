package com.oyetech.composebase.sharedScreens.messaging.conversationList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.helpers.viewProperties.OnResumeEffect
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListEvent.OnConversationClick
import com.oyetech.composebase.sharedScreens.messaging.views.MessageConversationItemView
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-17.02.2025-
-21:40-
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionName")
@Composable
fun MessageConversationListScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<MessageConversationListVm>()
    OnResumeEffect {
        vm.onEvent(MessageConversationListEvent.OnConversationScreenOpen)
    }
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val listViewState by vm.listViewState.collectAsStateWithLifecycle()


    BaseScaffold(
        topBar = {
            MessageConversationToolbar()
        },
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            MessageConversationListScreen(
                uiState = uiState,
                contentPadding = innerPadding,
                onEvent = { vm.onEvent(it) },
                listViewState = listViewState,
            )


            if (listViewState.isLoadingInitial) {
                LoadingScreenFullSize()
            }

            if (listViewState.isErrorInitial) {

                ErrorScreenFullSize(
                    errorText = listViewState.errorMessage,
                    withoutAlpha = true
                )
            }
            if (listViewState.isEmptyList) {
                ErrorScreenFullSize(
                    errorText = LanguageKey.conversationNotFound,
                    withoutAlpha = true
                )
            }
        })

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MessageConversationToolbar() {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = LanguageKey.messageConversationTitle,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MessageConversationToolbarPreview() {
    MessageConversationToolbar()

}

@Suppress("FunctionName")
@Composable
fun MessageConversationListScreen(
    contentPadding: PaddingValues,
    uiState: MessageConversationListUiState,
    onEvent: (MessageConversationListEvent) -> (Unit),
    listViewState: GenericListState<MessageConversationUiState>,
) {
    val lazyListState = rememberLazyListState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.padding(4.dp),
                state = lazyListState,
                reverseLayout = true,
                content = {
                    items(
                        items = listViewState.items,
                        key = { it.conversationId },
                        itemContent = { itemDetail ->
                            MessageConversationItemView(
                                modifier = Modifier.clickable {
                                    onEvent(
                                        OnConversationClick(
                                            conversationId = itemDetail.conversationId,
                                            userId = itemDetail.userId
                                        )
                                    )
                                },
                                uiState = itemDetail,
                            )
                        })
                },
            )
        }

    }
}