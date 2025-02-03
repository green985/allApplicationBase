package com.oyetech.composebase.projectQuotesFeature.quotes.uiState

import androidx.compose.ui.text.AnnotatedString

/**
Created by Erdi Özbek
-16.12.2024-
-23:14-
 **/

data class QuoteUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val text: String = "",
    val author: String = "",
    val createdAtString: String = "",
    val htmlFormatted: String = "",
    val authorImage: String = "",
    val quoteId: String = "",
    val annotatedStringText: AnnotatedString = AnnotatedString(""),
//    var charCount: Int = 0,
//    val tag : String = ""

)

sealed class QuoteListUiEvent {
    data class QuoteSeen(val index: Int) : QuoteListUiEvent()
//    object Refresh : QutoeListUiEvent()
}