package com.oyetech.repository.di

import com.oyetech.domain.repository.radioDataRepositories.RadioDataOperationRepository
import com.oyetech.domain.repository.radioDataRepositories.remoteRepositories.RadioCountryTagListRepository
import com.oyetech.domain.repository.radioDataRepositories.remoteRepositories.RadioStationListRepository
import com.oyetech.repository.imp.RadioDataOperationRepositoryImp
import com.oyetech.repository.imp.remote.RadioCountryTagListRepositoryImp
import com.oyetech.repository.imp.remote.RadioStationListRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-11.11.2024-
-11:08-
 **/

object RadioRepositoryDI {
    val repositoryModule = module {

        single<RadioDataOperationRepository> {
            RadioDataOperationRepositoryImp(
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }


        single<RadioStationListRepository> { RadioStationListRepositoryImp(get()) }
        single<RadioCountryTagListRepository> { RadioCountryTagListRepositoryImp(get()) }
    }
}