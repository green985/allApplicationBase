package com.oyetech.googlelogin.di

import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.googlelogin.GoogleLoginRepositoryImpl
import org.koin.dsl.module

/**
Created by Erdi Özbek
-17.06.2024-
-00:37-
 **/

object GoogleSignInModule {
    var googleSignInModulee = module {
        single<GoogleLoginRepository> { GoogleLoginRepositoryImpl(get()) }
    }
}