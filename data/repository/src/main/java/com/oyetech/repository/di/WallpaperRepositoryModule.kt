package com.oyetech.repository.di

import com.oyetech.domain.repository.wallpaperApp.WallpaperSearchOperationRepository
import com.oyetech.repository.wallpaperImp.wallpaper.search.WallpaperSearchOperationRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-18.09.2023-
-21:44-
 **/

object WallpaperRepositoryModule {

    var wallpaperRepositoryModule = module {
        single<WallpaperSearchOperationRepository> { WallpaperSearchOperationRepositoryImp(get()) }
    }
}
