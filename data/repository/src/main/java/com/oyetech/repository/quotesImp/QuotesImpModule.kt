package com.oyetech.repository.quotesImp

import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.repository.quotesImp.quotesData.QuotesRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-16.12.2024-
-22:23-
 **/

object QuotesImpModule {

    val quoteImpModule = module {
        single<QuotesRepository> { QuotesRepositoryImp(get()) }
        single<QuoteDataOperationRepository> {
            QuoteDataOperationRepositoryImp(
                get(),
                get(),
                get(),
                get()
            )
        }
    }
}