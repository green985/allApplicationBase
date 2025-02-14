package com.oyetech.composebase.projectQuotesFeature.contentOperation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
Created by Erdi Özbek
-14.02.2025-
-00:01-
 **/

@Composable
fun QuoteContentOperationView(
    modifier: Modifier = Modifier,
    contentOperationUiState: ContentOperationUiState,
    contentOperationEvent: (ContentOperationEvent) -> Unit,
) {
    // todo will be check for release
//    if (contentOperationUiState.contentId.isNotBlank()) {
    OutlinedCard(modifier = modifier) {
        Row(Modifier.padding(0.dp)) {
            IconButton(onClick = {
                contentOperationEvent(
                    ContentOperationEvent.LikeContent(
                        contentOperationUiState.contentId
                    )
                )
            }) {
                Icon(
                    modifier = Modifier
//                        .size(QuotesDimensions.contentIconHeightWidth)
                    ,
                    imageVector = if (contentOperationUiState.isLiked) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
//    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun QuoteContentOperationPrev() {
    QuoteContentOperationView(
        contentOperationUiState = ContentOperationUiState(
            contentId = "1",
            isLiked = false
        ),
        contentOperationEvent = {}
    )
}