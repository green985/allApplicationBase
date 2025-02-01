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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.oyetech.composebase.BuildConfig
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.loadingErrors.ErrorDialogFullScreen
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuotesFeature.QuotesDimensions
import com.oyetech.composebase.projectQuotesFeature.views.dialogs.GenericPopupMenuWithContentDialog
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.sharedScreens.quotes.tagList.QuoteTagUiState
import com.oyetech.models.quotes.responseModel.QuotesTagResponseData
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-29.01.2025-
-20:24-
 **/
@Suppress("FunctionName")
@Composable
fun AdviceQuoteScreenSetup(
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<AdviceQuoteVM>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val toolbarState by vm.toolbarUiState.collectAsStateWithLifecycle()
    val onEvent = { event: AdviceQuoteEvent -> vm.onEvent(event) }

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

    if (uiState.isExpandTagList) {
        GenericPopupMenuWithContentDialog(
            onEvent = { vm.onEvent(it) },
            onDismiss = { vm.onEvent(AdviceQuoteEvent.ToggleExpandTagList(false)) },
            tagList = uiState.tagListLarge,
            selectedTagList = uiState.selectedTagList
        )
    }


    if (uiState.isLoading) {
        LoadingScreenFullSize()
    }

    if (uiState.errorText.isNotEmpty()) {
        ErrorDialogFullScreen(
            errorMessage = uiState.errorText,
            onRetry = { onEvent(AdviceQuoteEvent.SubmitQuote) },
            onDismiss = { onEvent(AdviceQuoteEvent.ShowError("")) }
        )
    }
}

@Suppress("FunctionName", "LongMethod", "MagicNumber")

@Composable
fun AdviceQuoteScreen(uiState: AdviceQuoteUiState, onEvent: ((AdviceQuoteEvent) -> Unit)) {

    val scrollState = androidx.compose.foundation.rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tags", style = MaterialTheme.typography.bodyLarge)
                if (uiState.selectedTagList.size > 0) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = uiState.selectedTagList.size.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                LazyRow(
                    modifier = Modifier
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
                IconButton(modifier = Modifier.weight(1f), onClick = {
                    onEvent(AdviceQuoteEvent.ToggleExpandTagList(!uiState.isExpandTagList))
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand More"
                    )
                }
            }



            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.noteToInspector,
                onValueChange = { onEvent(AdviceQuoteEvent.UpdateNoteToInspector(it)) },
                label = {
                    Column {
                        Text("Note to Inspector", style = MaterialTheme.typography.bodyMedium)
                        Text("Optional*", style = MaterialTheme.typography.bodySmall)
                    }
                },
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

        if (BuildConfig.DEBUG) {
            FilledTonalButton(
                onClick = { onEvent(AdviceQuoteEvent.SetDummyData) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text("Set dummy data")
            }
        }

        FilledTonalButton(
            onClick = { onEvent(AdviceQuoteEvent.SubmitQuote) },
            enabled = uiState.isSendButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(QuotesDimensions.buttonHeight)
                .padding(bottom = 32.dp)
        ) {
            Text("Send Quote")
        }
    }


}

@Suppress("FunctionName", "UnusedPrivateMember", "MagicNumber")
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun AdviceQuotePreview() {
    AdviceQuoteScreen(
        AdviceQuoteUiState(
            isLoading = false,
            errorText = "",
            quoteText = "",
            authorList = persistentListOf(),
            selectedAuthor = null,
            authorText = "",
            noteToInspector = "",
            isExpandTagList = true,
            tagListSmall = QuotesTagResponseData.getTopicsList().subList(0, 4).map {
                QuoteTagUiState(it)
            }.toImmutableList(),
            tagListLarge = QuotesTagResponseData.getTopicsList().subList(4, 15).map {
                QuoteTagUiState(it)
            }.toImmutableList(),
            selectedTagList = persistentListOf(),
            inspectorOperationInfoText = "",
            isCheckedTruthForm = false,
            isSendButtonEnabled = false
        )
    ) {

    }
}