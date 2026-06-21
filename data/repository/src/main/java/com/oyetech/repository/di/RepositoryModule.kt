package com.oyetech.repository.di

import com.oyetech.domain.repository.randomOperation.RandomOperationRepository
import com.oyetech.domain.repository.stopwatch.StopwatchRecordRepository
import com.oyetech.repository.randomOperation.RandomOperationRepositoryImpl
import com.oyetech.repository.stopwatch.StopwatchRecordRepositoryImpl
import org.koin.dsl.module

object RepositoryModule {
    val module = module {
        single<RandomOperationRepository> { RandomOperationRepositoryImpl(get()) }
        single<StopwatchRecordRepository> { StopwatchRecordRepositoryImpl(get()) }
    }
}
