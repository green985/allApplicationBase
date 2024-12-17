package com.oyetech.core.di

import com.oyetech.core.coroutineHelper.AppDispatchers
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
            AppDispatchers(Dispatchers.Main, Dispatchers.IO)
        }
    }
}
