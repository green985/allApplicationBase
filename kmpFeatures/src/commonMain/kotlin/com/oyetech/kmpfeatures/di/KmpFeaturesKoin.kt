package com.oyetech.kmpfeatures.di

import com.oyetech.kmpfeatures.example.KmpFeaturesExampleOperation
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object KmpFeaturesKoin {
    val module: Module = module {
        singleOf(::KmpFeaturesExampleOperation)
    }
}
