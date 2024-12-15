package com.oyetech.languageModule.di

import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper
import org.koin.dsl.module

/**
Created by Erdi Özbek
-24/04/2024-
-19:33-
 **/

object LanguageModuleDi {
    val wallpaperLanguageModule = module(createdAtStart = true) {

        single {
            val sss = synchronized(this) {
                val languageHelper = LocalLanguageHelper(get())
                languageHelper
            }
            sss
        }
    }
}