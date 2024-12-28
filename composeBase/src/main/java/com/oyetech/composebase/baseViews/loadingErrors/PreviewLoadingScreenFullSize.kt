package com.oyetech.composebase.baseViews.loadingErrors

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 640)
@Composable
fun PreviewLoadingScreenFullSize() {
    LoadingScreenFullSize()
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 640)
@Composable
fun PreviewErrorScreenFullSize() {
    ErrorScreenFullSize(
        errorMessage = "Something went wrong!",
        onDismiss = { /* Dismiss Action */ },
        onRetry = { /* Retry Action */ }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 640)
@Composable
fun PreviewPagingMoreError() {
    PagingMoreError(
        errorMessage = "Failed to load more data",
        onRetry = { /* Retry Action */ }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 640)
@Composable
fun PreviewPagingMoreLoading() {
    PagingMoreLoading()
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 640)
@Composable
fun PreviewLoadingDialogFullScreen() {
    LoadingDialogFullScreen()
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 360, heightDp = 640)
@Composable
fun PreviewErrorDialogFullScreen() {
    ErrorDialogFullScreen(
        errorMessage = "An error occurred. Please try again.",
        onDismiss = { /* Dismiss Action */ },
        onRetry = { /* Retry Action */ }
    )
}
