package com.oyetech.composebase.projectQuotesFeature

import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteVM
import com.oyetech.composebase.projectQuotesFeature.authorListScreen.AuthorListVM
import com.oyetech.composebase.projectQuotesFeature.homeScreen.QuotesHomeScreenVm
import com.oyetech.composebase.sharedScreens.quotes.detail.QuoteDetailVm
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-6.01.2025-
-23:02-
 **/

object QuotesProjectModule {
    val module = module {
        viewModelOf(::QuotesHomeScreenVm)
        viewModelOf(::AuthorListVM)
        viewModelOf(::QuoteDetailVm)
        viewModelOf(::AdviceQuoteVM)
    }
}