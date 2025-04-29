package com.oyetech.composebase.projectQuotesFeature.authorListScreen

import com.oyetech.models.quotes.responseModel.QuoteAuthorResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

fun Flow<List<QuoteAuthorResponseData>>.mapToUiState(): Flow<List<QuoteAuthorUiState>> {
    return this.map {
        it.map {
            QuoteAuthorUiState(
                authorName = it.authorDisplayName,
                authorImage = it.authorImage,
                authorId = it.authorId,
            )
        }
    }
}