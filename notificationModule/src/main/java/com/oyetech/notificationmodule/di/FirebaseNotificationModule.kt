package com.oyetech.notificationmodule.di

import com.google.firebase.messaging.FirebaseMessaging
import com.oyetech.domain.repository.firebase.FirebaseTokenOperationRepository
import com.oyetech.notificationmodule.AppNotificationOperator
import com.oyetech.notificationmodule.tokenOperation.FirebaseTokenOperationRepositoryImpl
import org.koin.dsl.module

/**
Created by Erdi Özbek
-15.04.2025-
-21:02-
 **/

object FirebaseNotificationModule {
    val module = module {
        single<FirebaseMessaging> {
            val firebaseMessaging = FirebaseMessaging.getInstance()
            firebaseMessaging
        }
        single<FirebaseTokenOperationRepository> { FirebaseTokenOperationRepositoryImpl(get()) }
        single<AppNotificationOperator> { AppNotificationOperator(get()) }
    }
}