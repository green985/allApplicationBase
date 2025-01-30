package com.oyetech.composebase.projectQuotesFeature.adviceQuote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
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
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.sharedScreens.quotes.tagList.QuoteTagUiState
import com.oyetech.models.quotes.responseModel.QuotesTagResponseData
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-29.01.2025-
-20:24-
 **/

@Composable
fun AdviceQuoteScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<AdviceQuoteVM>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val toolbarState by vm.toolbarUiState.collectAsStateWithLifecycle()

    BaseScaffold(topBarContent = {
        QuoteToolbarSetup(toolbarState) {
            when (it) {
                is QuoteToolbarEvent.BackButtonClick -> {
                    navigationRoute("back")
                }

                else -> {}
            }
        }
    }) {
        Column(modifier = Modifier.padding(it)) {
            AdviceQuoteScreen(uiState, vm::onEvent)
        }
    }

}

@Composable
fun AdviceQuoteScreen(uiState: AdviceQuoteUiState, onEvent: ((AdviceQuoteEvent) -> Unit)) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            OutlinedTextField(
                value = uiState.quoteText,
                onValueChange = { onEvent(AdviceQuoteEvent.UpdateQuoteText(it)) },
                label = { Text("Quote") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.authorText,
                onValueChange = { onEvent(AdviceQuoteEvent.UpdateAuthorText(it)) },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tags", style = MaterialTheme.typography.bodyLarge)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.tagListSmall,
                    key = { it.tagName },
                    itemContent = { tag ->
                        InputChip(
                            modifier = Modifier.weight(1f),
                            selected = uiState.selectedTagList.contains(tag),
                            onClick = {
                                if (uiState.selectedTagList.contains(tag)) {
                                    onEvent(AdviceQuoteEvent.RemoveTag(tag))
                                } else {
                                    onEvent(AdviceQuoteEvent.SelectTag(tag))
                                }
                            },
                            label = { Text(tag.tagName) }
                        )
                    }
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.noteToInspector,
                onValueChange = { onEvent(AdviceQuoteEvent.UpdateNoteToInspector(it)) },
                label = { Text("Note to Inspector") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.isCheckedTruthForm,
                    onCheckedChange = { onEvent(AdviceQuoteEvent.ToggleTruthForm(it)) }
                )
                Text("I confirm this quote is true")
            }
        }

        Button(
            onClick = { onEvent(AdviceQuoteEvent.SubmitQuote) },
            enabled = uiState.isSendButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Quote")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (uiState.isLoading) {
        LoadingScreenFullSize()
    }

    if (uiState.isErrorText.isNotEmpty()) {
        ErrorScreenFullSize(
            errorMessage = uiState.isErrorText,
            onRetry = { onEvent(AdviceQuoteEvent.SubmitQuote) }
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun AdviceQuotePreview() {
    AdviceQuoteScreen(
        AdviceQuoteUiState(
            isLoading = false,
            isErrorText = "",
            quoteText = "",
            authorList = listOf(),
            selectedAuthor = null,
            authorText = "",
            noteToInspector = "",
            isExpandTagList = false,
            tagListSmall = QuotesTagResponseData.getTopicsList().subList(0, 4).map {
                QuoteTagUiState(it)
            },
            tagListLarge = listOf(),
            selectedTagList = listOf(),
            inspectorOperationInfoText = "",
            isCheckedTruthForm = false,
            isSendButtonEnabled = false
        )
    ) {

    }
}