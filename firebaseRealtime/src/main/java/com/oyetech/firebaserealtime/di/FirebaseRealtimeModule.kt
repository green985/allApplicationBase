package com.oyetech.firebaserealtime.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger.Level.DEBUG
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepository
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepositoryDeneme
import com.oyetech.firebaserealtime.FirebaseRealtimeHelperRepositoryDenemeImpl
import org.koin.dsl.module

/**
Created by Erdi Özbek
-18.02.2025-
-19:42-
 **/

object FirebaseRealtimeModule {
    val module = module {
        single<FirebaseDatabase> {
            val database = FirebaseDatabase.getInstance()
            database.setPersistenceEnabled(true)
            database.setLogLevel(DEBUG)
            database
        }

        single<FirebaseRealtimeHelperRepositoryDeneme> {
            FirebaseRealtimeHelperRepositoryDenemeImpl(
                get()
            )
        }
        single<FirebaseRealtimeHelperRepository> {
            FirebaseRealtimeHelperRepositoryImpl(
                get(),
                get(), get()
            )
        }
    }
}
