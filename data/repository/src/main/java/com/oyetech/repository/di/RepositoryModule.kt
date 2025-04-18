package com.oyetech.repository.di

import com.oyetech.domain.repository.contentOperation.ContentOperationLocalRepository
import com.oyetech.domain.repository.firebase.FirebaseCloudOperationRepository
import com.oyetech.domain.repository.messaging.MessagesSendingOperationRepository
import com.oyetech.domain.repository.messaging.local.MessagesAllLocalDataSourceRepository
import com.oyetech.repository.contentOperation.ContentOperationLocalRepositoryImp
import com.oyetech.repository.firebaseCloud.FirebaseCloudOperationRepositoryImp
import com.oyetech.repository.firebaseMessaging.MessagesAllLocalDataSourceImp
import com.oyetech.repository.firebaseMessaging.MessagesSendingOperationRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-20.02.2025-
-22:15-
 **/

object RepositoryModule {
    val module = module {
        single<MessagesSendingOperationRepository> { MessagesSendingOperationRepositoryImp(get()) }
        single<MessagesAllLocalDataSourceRepository> { MessagesAllLocalDataSourceImp(get()) }
        single<ContentOperationLocalRepository> { ContentOperationLocalRepositoryImp(get()) }

        single<FirebaseCloudOperationRepository> { FirebaseCloudOperationRepositoryImp(get()) }

    }
}