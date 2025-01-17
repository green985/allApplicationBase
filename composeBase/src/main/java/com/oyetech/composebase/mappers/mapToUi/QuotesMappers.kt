package com.oyetech.composebase.mappers.mapToUi

import androidx.core.text.parseAsHtml
import com.oyetech.composebase.helpers.viewProperties.toAnnotatedString
import com.oyetech.composebase.sharedScreens.quotes.uiState.QuoteUiState
import com.oyetech.models.quotes.responseModel.QuoteResponseData

/**
Created by Erdi Özbek
-17.01.2025-
-20:51-
 **/
object QuotesMappers {

    fun mapToQuoteUiState(it: QuoteResponseData) =
        QuoteUiState(
            quoteId = it.quoteId,
            text = it.text,
            author = it.author,
//            createdAtString = TimeFunctions.getDateFromLongWithoutHour(it.createdAt),
            createdAtString = "",
            authorImage = it.authorImage,
            htmlFormatted = it.htmlFormatted,
            annotatedStringText = it.htmlFormatted.parseAsHtml().toAnnotatedString()
        )
}
