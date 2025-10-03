package com.oyetech.domain.di

import com.oyetech.domain.helper.ActivityProviderUseCase
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioDataOperationUseCase
import com.oyetech.domain.radioOperationUseCases.remoteUseCase.RadioStationListOperationUseCase
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepository
import com.oyetech.domain.repository.messaging.MessagesAllOperationRepositoryImp
import com.oyetech.domain.repository.usernameGeneratorRepository.UsernameGeneratorImp
import com.oyetech.domain.repository.usernameGeneratorRepository.UsernameGeneratorRepository
import com.oyetech.domain.useCases.AdsHelperUseCase
import com.oyetech.domain.useCases.AlarmOperationUseCase
import com.oyetech.domain.useCases.AnalyticsOperationUseCase
import com.oyetech.domain.useCases.GlideOperationUseCase
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.domain.useCases.helpers.AppReviewOperationUseCase
import com.oyetech.domain.useCases.remoteUseCase.RadioCountryTagOperationUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-28.02.2022-
-22:53-
 **/

object DomainModule {

    val wallpaperDomainModule = module {
        singleOf(::ActivityProviderUseCase)
        single { GlideOperationUseCase(get()) }
        singleOf(::AdsHelperUseCase)
        // singleOf(::ContentOperationUseCase)

//        single { SharedOperationUseCase(get()) }
//
//        single { AlarmOperationUseCase(get()) }
        single { GlideOperationUseCase(get()) }
//        single { SharedPrefUseCase(get()) }
        single { AppReviewOperationUseCase(get(), get()) }
//        single { LocalNotificationUseCase(get()) }
//        single { DynamicLinkOperationUseCase(get()) }
        single { AnalyticsOperationUseCase(get()) }
        single<UsernameGeneratorRepository> { UsernameGeneratorImp() }
    }
}

object RadioDomainModule {
    val module = module {
        single { RadioDataOperationUseCase(get()) }
        single { RadioOperationUseCase(get(), get(), get()) }
        single { RadioStationListOperationUseCase(get(), get()) }
        single { RadioCountryTagOperationUseCase(get()) }
        single { AlarmOperationUseCase(get()) }
    }
}

object QuoteDomainModule {
    val module = module {
        single<MessagesAllOperationRepository> {
            MessagesAllOperationRepositoryImp(
                get(),
                get(),
                get()
            )
        }
        single { NavigationUseCase() }
    }
}

