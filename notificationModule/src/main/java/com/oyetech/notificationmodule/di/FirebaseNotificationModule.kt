package com.oyetech.notificationmodule.di

import com.google.firebase.messaging.FirebaseMessaging
import com.oyetech.domain.repository.NotificationHandlerRepository
import com.oyetech.domain.repository.firebase.FirebaseNotificationTokenOperationRepository
import com.oyetech.notificationmodule.AppNotificationOperator
import com.oyetech.notificationmodule.NotificationHandlerHelper
import com.oyetech.notificationmodule.NotificationHandlerRepositoryImpl
import com.oyetech.notificationmodule.tokenOperation.FirebaseNotificationNotificationTokenOperationRepositoryImpl
import org.koin.core.module.dsl.singleOf
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
        single<FirebaseNotificationTokenOperationRepository> {
            FirebaseNotificationNotificationTokenOperationRepositoryImpl(
                get()
            )
        }
        single<AppNotificationOperator> { AppNotificationOperator(get()) }
        singleOf(::NotificationHandlerHelper)
        single<NotificationHandlerRepository> { NotificationHandlerRepositoryImpl(get()) }
    }
}
