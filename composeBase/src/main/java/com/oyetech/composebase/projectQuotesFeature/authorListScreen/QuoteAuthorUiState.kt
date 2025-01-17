package com.oyetech.composebase.projectQuotesFeature.authorListScreen

/**
Created by Erdi Özbek
-11.01.2025-
-16:15-
 **/

data class QuoteAuthorUiState(
    val authorName: String,
    val authorImage: String,
    val authorId: String,
    val authorDescription: String = "",
    val authorQuotesCount: Int = 0,
)

sealed class QuoteAuthorEvent {
    data class AuthorClicked(val authorId: String) : QuoteAuthorEvent()

}