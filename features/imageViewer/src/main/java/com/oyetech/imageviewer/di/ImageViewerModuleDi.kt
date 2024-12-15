package com.oyetech.imageviewer.di

import com.oyetech.domain.repository.viewHelperRepositories.ImageViewerRepository
import com.oyetech.imageviewer.ImageViewerRepositoryImp
import com.oyetech.imageviewer.ImageViewerVm
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-16.05.2022-
-13:23-
 **/

object ImageViewerModuleDi {

    val imageViewerModule = module {
        viewModelOf(::ImageViewerVm)

        single<ImageViewerRepository> { ImageViewerRepositoryImp() }

    }
}
