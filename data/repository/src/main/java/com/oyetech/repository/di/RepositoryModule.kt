package com.oyetech.repository.di

import com.oyetech.domain.repository.firebase.FirebaseCloudOperationRepository
import com.oyetech.domain.repository.randomOperation.RandomOperationRepository
import com.oyetech.repository.firebaseCloud.FirebaseCloudOperationRepositoryImp
import com.oyetech.repository.randomOperation.RandomOperationRepositoryImpl
import org.koin.dsl.module

/**
Created by Erdi Özbek
-20.02.2025-
-22:15-
 **/

object RepositoryModule {
    val module = module {
        single<FirebaseCloudOperationRepository> { FirebaseCloudOperationRepositoryImp(get()) }
        single<RandomOperationRepository> { RandomOperationRepositoryImpl(get()) }
    }
}
