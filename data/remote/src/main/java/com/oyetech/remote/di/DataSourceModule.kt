package com.oyetech.remote.di

import com.oyetech.remote.wallpaperRemote.dataSource.BibleDataSource
import com.oyetech.remote.wallpaperRemote.dataSource.BibleReadOperationDataSource
import com.oyetech.remote.wallpaperRemote.dataSource.ChurchesDataSource
import com.oyetech.remote.wallpaperRemote.dataSource.GeneralOperationDataSource
import com.oyetech.remote.wallpaperRemote.dataSource.LanguageCountryDataSource
import org.koin.dsl.module

/**
Created by Erdi Özbek
-19.09.2023-
-14:00-
 **/

var dataSourceModule = module {

    single { ChurchesDataSource(get()) }
    single { LanguageCountryDataSource(get()) }
    single { BibleDataSource(get(), get()) }
    single { BibleReadOperationDataSource(get()) }
    single { GeneralOperationDataSource(get()) }

}