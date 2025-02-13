package com.oyetech.composebase.projectQuotesFeature.contentOperation

/**
Created by Erdi Özbek
-13.02.2025-
-23:47-
 **/

data class ContentOperationUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val isLiked: Boolean = false,
    val contentId: String,
)

sealed class ContentOperationEvent {
    data class LikeContent(val contentId: String) : ContentOperationEvent()
//    object Idlee : ContentOperationEvent()
}