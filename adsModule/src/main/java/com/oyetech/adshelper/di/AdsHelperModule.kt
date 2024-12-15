package com.oyetech.adshelper.di

import com.oyetech.adshelper.di.impl.AdsHelperRepositoryImp
import com.oyetech.domain.repository.AdsHelperRepository
import org.koin.dsl.module

/**
Created by Erdi Özbek
-23.01.2023-
-23:25-
 **/
object AdsHelperModule {

    val adsHelperModulee = module {
        single<AdsHelperRepository> { AdsHelperRepositoryImp() }
    }
}
