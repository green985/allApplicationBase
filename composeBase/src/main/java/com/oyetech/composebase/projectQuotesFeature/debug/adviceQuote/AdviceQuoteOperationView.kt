package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectQuotesFeature.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import com.oyetech.models.quotes.enums.AdviceQuoteStatusEnum.Approved

/**
Created by Erdi Özbek
-3.02.2025-
-23:48-
 **/

@Composable
fun AdviceQuoteOperationView(
    modifier: Modifier = Modifier,
    uiState: ItemAdviceQuoteDebugUiState,
    onEvent: (AdviceQuoteDebugEvent) -> (Unit),
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .then(
                if (uiState.status == Approved) Modifier.background(
                    color = androidx.compose.ui.graphics.Color.Green
                ) else Modifier.background(
                    color = androidx.compose.ui.graphics.Color.Black
                )
            )
    ) {
        RandomQuotesSmallView(
            uiState = QuoteUiState(
                text = uiState.text,
                author = uiState.author,
            ),
            navigationRoute = {}
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { onEvent(AdviceQuoteDebugEvent.RejectQuote(uiState.documentId)) }) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "RejectQuote"
                )
            }
            Spacer(modifier = Modifier.width(50.dp))
            IconButton(onClick = { onEvent(AdviceQuoteDebugEvent.ApproveQuote(uiState.documentId)) }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "ApproveQuote"
                )
            }
//            IconButton(onClick = { onEvent(AdviceQuoteDebugEvent.) }) {
//                Icon(
//                    painter = painterResource(id = ),
//                    contentDescription = "Favorite"
//                )
//            }
        }
    }


}

// preview
@Preview
@Composable
fun AdviceQuoteOperationViewPreview() {
    AdviceQuoteOperationView(
        Modifier,
        ItemAdviceQuoteDebugUiState(
            text = "Text",
            author = "Author",
            status = Approved,
            documentId = "DocumentId"
        )
    ) {}
}