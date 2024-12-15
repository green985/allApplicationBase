package com.oyetech.radioservice.di

import android.content.Context
import com.oyetech.domain.repository.AlarmOperationRepository
import com.oyetech.domain.useCases.contentOperations.ExoPlayerOperationUseCase
import com.oyetech.radioservice.alarm.AlarmManagerHelper
import com.oyetech.radioservice.services.PlayerService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.dsl.module
import timber.log.Timber

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
            scoped { DenemeClasss(get()) }
        }

        single { ExoPlayerOperationUseCase(get()) }

        single<AlarmOperationRepository> { AlarmManagerHelper(get(), get()) }
    }
}

class DenemeClasss(var context: Context) {

    fun startAsdasdasd() {
        GlobalScope.launch {
            repeat(1000000) {
                delay(1000)
                Timber.d("asdasd == " + it)
            }
        }
    }

}