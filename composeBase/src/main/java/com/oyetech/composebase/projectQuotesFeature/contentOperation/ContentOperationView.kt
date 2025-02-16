package com.oyetech.composebase.projectQuotesFeature.contentOperation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.baseViews.loadingErrors.ErrorDialogFullScreen
import com.oyetech.composebase.baseViews.loadingErrors.LoadingDialogFullScreen
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent.LikeContent

/**
Created by Erdi Özbek
-14.02.2025-
-00:01-
 **/

@Suppress("FunctionNaming")
@Composable
fun QuoteContentOperationView(
    modifier: Modifier = Modifier,
    contentOperationUiState: ContentOperationUiState,
    contentOperationEvent: (ContentOperationEvent) -> Unit,
) {
    OutlinedCard(modifier = modifier) {
        Row(Modifier.padding(0.dp)) {
            IconButton(
                enabled = contentOperationUiState.isInitialed,
                onClick = {
                    contentOperationEvent(
                        LikeContent(
                            contentOperationUiState.contentId
                        )
                    )
                }) {
                Icon(
                    modifier = Modifier,
                    imageVector = if (contentOperationUiState.isLiked) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = null,
                    tint = if (contentOperationUiState.isInitialed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        LocalContentColor.current
                    }
                )
            }
        }
    }

    if (contentOperationUiState.isLoading) {
        LoadingDialogFullScreen()
    }

    if (contentOperationUiState.errorText.isNotBlank()) {
        ErrorDialogFullScreen(
            errorMessage = contentOperationUiState.errorText,
        )
    }

}

@Suppress("FunctionName", "UnusedPrivateMember")
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun QuoteContentOperationPrev() {
    QuoteContentOperationView(
        contentOperationUiState = ContentOperationUiState(
            contentId = "1",
            isInitialed = false,
            isLiked = false
        ),
        contentOperationEvent = {}
    )
}