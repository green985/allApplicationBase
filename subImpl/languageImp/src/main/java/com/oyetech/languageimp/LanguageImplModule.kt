package com.oyetech.languageimp

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-18.01.2025-
-17:32-
 **/

object LanguageImplModule {
    val languageImplModule = module {
        singleOf(::LanguageOperationHelper)
    }
}