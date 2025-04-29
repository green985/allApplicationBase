package com.oyetech.tools.di

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

/**
Created by Erdi Özbek
-10.04.2022-
-17:20-
 **/
object CommonsModule {
    val module = module {
        single {
            com.oyetech.tools.coroutineHelper.AppDispatchers(Dispatchers.Main, Dispatchers.IO)
        }
    }
}
