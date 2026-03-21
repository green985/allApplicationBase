package com.oyetech.radioservice.di

import com.oyetech.domain.useCases.contentOperations.ExoPlayerOperationUseCase
import com.oyetech.radioservice.services.PlayerService
import org.koin.dsl.module

/**
Created by Erdi Özbek
-21.11.2022-
-00:08-
 **/
object RadioServiceModule {

    var serviceModule = module {

        /*
        scope<PlayerServiceHelper> {
            scoped { ExoPlayerOperationUseCase(get()) }
        }


         */

        scope<PlayerService> {
        }

        single { ExoPlayerOperationUseCase(get()) }
    }
}
