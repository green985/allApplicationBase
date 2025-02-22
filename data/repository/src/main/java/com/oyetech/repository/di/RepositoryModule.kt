package com.oyetech.repository.di

import com.oyetech.domain.repository.firebase.FirebaseMessagingLocalRepository
import com.oyetech.repository.firebaseMessaging.FirebaseMessagingLocalRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-20.02.2025-
-22:15-
 **/

object RepositoryModule {
    val module = module {
        single<FirebaseMessagingLocalRepository> { FirebaseMessagingLocalRepositoryImp(get()) }
    }
}