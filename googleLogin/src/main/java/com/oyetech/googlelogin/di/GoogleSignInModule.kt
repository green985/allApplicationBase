package com.oyetech.googlelogin.di

import com.oyetech.domain.repository.loginOperation.AuthOperationRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginOperationRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.googlelogin.AuthOperationRepositoryImpl
import com.oyetech.googlelogin.GoogleLoginOperationRepositoryImpl
import com.oyetech.googlelogin.GoogleLoginRepositoryImpl3
import org.koin.dsl.module

/**
Created by Erdi Özbek
-17.06.2024-
-00:37-
 **/

object GoogleSignInModule {
    var googleSignInModulee = module {
//        single<GoogleLoginRepository> { GoogleLoginRepositoryImpl(get(), get(), get()) }
//        single<GoogleLoginRepository> { GoogleLoginRepositoryImpl2(get(), get(), get()) }
        single<GoogleLoginRepository> { GoogleLoginRepositoryImpl3(get(), get()) }
        single<GoogleLoginOperationRepository> { GoogleLoginOperationRepositoryImpl(get(), get()) }
        single<AuthOperationRepository> { AuthOperationRepositoryImpl(get(), get(), get()) }

//        single<GoogleLoginRepository> { FirebaseGoogleLoginRepositoryImpl(get(), get()) }
    }
}
