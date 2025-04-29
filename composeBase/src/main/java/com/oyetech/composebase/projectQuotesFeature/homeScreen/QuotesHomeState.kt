package com.oyetech.composebase.projectQuotesFeature.homeScreen

import com.oyetech.models.quotes.enums.QuoteListEnum
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class QuotesHomeState(
    val isLoading: Boolean = false,
) {
    val tabEnumList: ImmutableList<QuoteListEnum> =
        persistentListOf(QuoteListEnum.All, QuoteListEnum.Random)
}

object QuotesHomeEvent {

}
