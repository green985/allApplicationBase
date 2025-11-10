package com.oyetech.repository.di

import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.repository.question.QuestionSupabaseRepositoryImpl
import org.koin.dsl.module

/**
Created by Erdi Özbek
-18.09.2023-
-21:44-
 **/

object QuestionRepositoryModule {

    var questionRepositoryModule = module {
        single<QuestionSupabaseRepository> { QuestionSupabaseRepositoryImpl(get()) }
    }
}
