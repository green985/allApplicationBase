package com.oyetech.domain.di

import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepositoryImp
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.domain.useCases.remoteUseCase.RadioCountryTagOperationUseCase
import org.koin.dsl.module

/**
Created by Erdi Özbek
-28.02.2022-
-22:53-
 **/

object RadioDomainModule {
    val module = module {
        single { RadioOperationUseCase(get()) }
        single { RadioStationListOperationUseCase(get()) }
        single { RadioCountryTagOperationUseCase(get()) }
        single<MessagesAllOperationRepository> { MessagesAllOperationRepositoryImp(get()) }
    }
}
