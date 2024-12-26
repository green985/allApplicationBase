package com.oyetech.composebase.projectRadioFeature.views.quotes.uiState

import androidx.compose.ui.text.AnnotatedString

/**
Created by Erdi Özbek
-16.12.2024-
-23:14-
 **/

data class QuotesUiState(
    val quoteId: String = "",
    val text: String = "",
    val author: String = "",
    val createdAtString: String = "",
    val authorImage: String = "",
    val htmlFormatted: String = "",
    val annotatedStringText: AnnotatedString = AnnotatedString(""),
//    var charCount: Int = 0,
//    val tag : String = ""

)

sealed class QuoteListUiEvent {
    data class LoadMore(val currentItem: Int) : QuoteListUiEvent()
    data class QuoteSeen(val quotesUiState: QuotesUiState) : QuoteListUiEvent()
//    object Refresh : QutoeListUiEvent()
}