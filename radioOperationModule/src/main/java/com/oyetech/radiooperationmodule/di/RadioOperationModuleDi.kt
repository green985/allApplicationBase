package com.oyetech.radiooperationmodule.di

import com.oyetech.domain.repository.contentOperation.RadioOperationRepository
import com.oyetech.radiooperationmodule.radioo.RadioRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-15.11.2022-
-17:40-
 **/
object RadioOperationModuleDi {

    var radioModule = module {
        single<RadioOperationRepository> { RadioRepositoryImp(get()) }
    }
}
