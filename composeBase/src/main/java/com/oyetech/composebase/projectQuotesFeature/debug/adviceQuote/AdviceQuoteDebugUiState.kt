package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote

import com.oyetech.models.quotes.enums.AdviceQuoteStatusEnum
import com.oyetech.models.quotes.enums.toAdviceQuoteStatusEnum
import com.oyetech.models.quotes.responseModel.AdviceQuoteResponseData
import com.oyetech.tools.stringHelper.StringHelper.toUniqString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-3.02.2025-
-22:29-
 **/

data class AdviceQuoteDebugUiState(
    val isLoading: Boolean = false,
    val list: ImmutableList<ItemAdviceQuoteDebugUiState> = persistentListOf(),
    val errorMessage: String = "",
)

data class ItemAdviceQuoteDebugUiState(
    val text: String = "",
    val documentId: String = "",
    val quoteId: String = text.toUniqString(),
    val author: String = "",
    var tags: List<String> = emptyList(),
    var status: AdviceQuoteStatusEnum = AdviceQuoteStatusEnum.Idle,
)

sealed class AdviceQuoteDebugEvent {
    data class ApproveQuote(val id: String) : AdviceQuoteDebugEvent()
    data class RejectQuote(val id: String) : AdviceQuoteDebugEvent()

    //    object Idlee : AdviceQuoteDebugEvent()
    object FilterNotApproved : AdviceQuoteDebugEvent()
    object FilterApproved : AdviceQuoteDebugEvent()
    object FilterAll : AdviceQuoteDebugEvent()
}

fun AdviceQuoteResponseData.mapToUiState(): ItemAdviceQuoteDebugUiState {
    return ItemAdviceQuoteDebugUiState(
        text = this.quoteText,
        documentId = this.documentId,
        author = this.author,
        tags = this.tags,
        status = this.status.toAdviceQuoteStatusEnum()
    )
}