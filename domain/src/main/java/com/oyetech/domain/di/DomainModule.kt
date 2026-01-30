package com.oyetech.domain.di

import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.useCases.AlarmOperationUseCase
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
        single { RadioDataOperationUseCase(get()) }
        single { RadioOperationUseCase(get(), get(), get()) }
        single { RadioStationListOperationUseCase(get(), get()) }
        single { RadioCountryTagOperationUseCase(get()) }
        single { AlarmOperationUseCase(get()) }
    }
}
