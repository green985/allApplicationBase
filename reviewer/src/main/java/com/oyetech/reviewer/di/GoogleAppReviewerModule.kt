package com.oyetech.reviewer.di

import com.oyetech.domain.repository.helpers.AppReviewControllerRepository
import com.oyetech.domain.repository.helpers.AppReviewRepository
import com.oyetech.domain.repository.helpers.AppReviewResultRepository
import com.oyetech.reviewer.AppReviewResultRepositoryImp
import com.oyetech.reviewer.GooglePlayReviewerHelperImp
import com.oyetech.reviewer.ReviewOperationController
import org.koin.dsl.module

/**
Created by Erdi Özbek
-5.02.2024-
-15:00-
 **/

object GoogleAppReviewerModule {

    var googlePlayReviewerModule = module {
        single<AppReviewControllerRepository> { ReviewOperationController(get()) }
        single<AppReviewRepository> { GooglePlayReviewerHelperImp(get(), get()) }
        single<AppReviewResultRepository> { AppReviewResultRepositoryImp() }
    }
}