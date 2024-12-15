package com.oyetech.glidemodule

import com.oyetech.domain.repository.GlideOperationRepository
import org.koin.dsl.module

/**
Created by Erdi Özbek
-18.11.2024-
-18:33-
 **/

object GlideModuleDi {
    val glideModule = module {
        single<GlideOperationRepository> { GlideOperationRepositoryImp(get()) }
    }
}