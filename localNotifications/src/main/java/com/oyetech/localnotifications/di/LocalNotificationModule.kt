package com.oyetech.localnotifications.di

import com.oyetech.domain.repository.LocalNotificationRepository
import com.oyetech.localnotifications.impl.LocalNotificationRepositoryImplNew
import org.koin.dsl.module

/**
Created by Erdi Özbek
-11.04.2023-
-18:37-
 **/

var localNotificationModule = module {
    single<LocalNotificationRepository> { LocalNotificationRepositoryImplNew(get(), get()) }
}