package com.oyetech.composebase.projectQuotesFeature.adviceQuote

import com.oyetech.composebase.projectQuotesFeature.authorListScreen.QuoteAuthorUiState
import com.oyetech.composebase.projectQuotesFeature.quotes.tagList.QuoteTagUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-29.01.2025-
-20:20-
 **/

data class AdviceQuoteUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val quoteText: String = "",

    val authorList: ImmutableList<QuoteAuthorUiState> = persistentListOf(),
    val selectedAuthor: QuoteAuthorUiState? = null,
    val authorText: String = "",

    val noteToInspector: String = "",

    val isExpandTagList: Boolean = false,
    val tagListSmall: ImmutableList<QuoteTagUiState> = persistentListOf(),
    val tagListLarge: ImmutableList<QuoteTagUiState> = persistentListOf(),
    val selectedTagList: ImmutableList<QuoteTagUiState> = persistentListOf(),

    val inspectorOperationInfoText: String = "", // info text for the user about what for this field

    val isCheckedTruthForm: Boolean = false,
    val isSendButtonEnabled: Boolean = false,

    )

sealed class AdviceQuoteEvent {
    data class SetLoading(val isLoading: Boolean) : AdviceQuoteEvent()
    data class ShowError(val errorText: String) : AdviceQuoteEvent()
    data class UpdateQuoteText(val quoteText: String) : AdviceQuoteEvent()

    data class UpdateAuthorList(val authorList: List<QuoteAuthorUiState>) : AdviceQuoteEvent()
    data class SelectAuthor(val selectedAuthor: QuoteAuthorUiState?) : AdviceQuoteEvent()
    data class UpdateAuthorText(val authorText: String) : AdviceQuoteEvent()

    data class ToggleExpandTagList(val isExpanded: Boolean) : AdviceQuoteEvent()
    data class UpdateTagListSmall(val tagList: List<QuoteTagUiState>) : AdviceQuoteEvent()
    data class UpdateTagListLarge(val tagList: List<QuoteTagUiState>) : AdviceQuoteEvent()
    data class SelectTag(val tag: QuoteTagUiState) : AdviceQuoteEvent()
    data class RemoveTag(val tag: QuoteTagUiState) : AdviceQuoteEvent()

    data class UpdateNoteToInspector(val noteToInspector: String) : AdviceQuoteEvent()
    data class ToggleTruthForm(val isChecked: Boolean) : AdviceQuoteEvent()

    object SubmitQuote : AdviceQuoteEvent()
    object SetDummyData : AdviceQuoteEvent()
}