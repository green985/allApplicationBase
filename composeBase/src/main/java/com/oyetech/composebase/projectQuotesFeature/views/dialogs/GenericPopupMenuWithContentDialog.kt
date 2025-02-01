package com.oyetech.composebase.projectQuotesFeature.views.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.helpers.viewProperties.DialogHelper
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.RemoveTag
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.SelectTag
import com.oyetech.composebase.sharedScreens.quotes.tagList.QuoteTagUiState

@Composable
fun GenericPopupMenuWithContentDialog(
    onEvent: (AdviceQuoteEvent) -> Unit,
    onDismiss: () -> Unit = {},
    tagList: List<QuoteTagUiState>,
    selectedTagList: List<QuoteTagUiState>,
) {
    androidx.compose.ui.window.Dialog(
        properties = DialogHelper.fullScreenDialogPropertiesCanDismiss,
        onDismissRequest = {
            onDismiss()
        }) {
        Box(
            modifier = Modifier
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                columns = Fixed(3), // Her satırda 3 öğe olacak şekilde
            ) {
                items(
                    items = tagList,
                    key = { it.tagName },
                    itemContent = { tag ->
                        InputChip(
                            modifier = Modifier,
                            selected = selectedTagList.contains(tag),
                            onClick = {
                                if (selectedTagList.contains(tag)) {
                                    onEvent(RemoveTag(tag))
                                } else {
                                    onEvent(SelectTag(tag))
                                }
                            },
                            label = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = tag.tagName, textAlign = TextAlign.Center
                                )
                            }
                        )

                    }
                )

            }
        }
    }
}
