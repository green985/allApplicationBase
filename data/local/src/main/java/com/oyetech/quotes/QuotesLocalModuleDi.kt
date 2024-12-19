package com.oyetech.quotes

import com.oyetech.quotes.database.QuoteAllListDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
Created by Erdi Özbek
-18.12.2024-
-02:16-
 **/

object QuotesLocalModuleDi {

    private const val QUOTE_ALL_LIST_DATABASE = "QUOTE_ALL_LIST_DATABASE"

    val localModule = module {
        single(named(QUOTE_ALL_LIST_DATABASE)) { QuoteAllListDatabase.buildDatabase(androidContext()) }
        factory { (get(named(QUOTE_ALL_LIST_DATABASE)) as QuoteAllListDatabase).quotesAllListDao() }

    }
}