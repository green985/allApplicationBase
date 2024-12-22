package com.oyetech.firebaseDB.di

import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseDBUserOperationRepository
import com.oyetech.domain.repository.firebase.RadioAnalyticsOperationRepository
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import com.oyetech.firebaseDB.firebaseDB.FirebaseDBOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.comment.FirebaseCommentOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.radio.RadioAnalyticsOperationRepositoryImp
import com.oyetech.firebaseDB.userOperation.FirebaseDBUserOperationRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-11.04.2023-
-18:37-
 **/

object FirebaseDBModule {

    var firebaseDBModulee = module {
        single<FirebaseContactWithMeOperationRepository> { FirebaseDBOperationRepositoryImp() }
        single<FirebaseDBUserOperationRepository> { FirebaseDBUserOperationRepositoryImp() }
        single<RadioAnalyticsOperationRepository> { RadioAnalyticsOperationRepositoryImp() }
        single<FirebaseCommentOperationRepository> { FirebaseCommentOperationRepositoryImp() }
    }
}
