package com.oyetech.firebaseDB.di

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseDBUserOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.RadioAnalyticsOperationRepository
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import com.oyetech.firebaseDB.firebaseDB.FirebaseDBOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.comment.FirebaseCommentOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.radio.RadioAnalyticsOperationRepositoryImp
import com.oyetech.firebaseDB.userOperation.FirebaseDBUserOperationRepositoryImp
import com.oyetech.firebaseDB.userOperation.FirebaseUserRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-11.04.2023-
-18:37-
 **/

object FirebaseDBModule {

    var firebaseDBModulee = module {
        single<FirebaseFirestore> { FirebaseFirestore.getInstance() }
        single<FirebaseContactWithMeOperationRepository> { FirebaseDBOperationRepositoryImp(get()) }
        single<FirebaseDBUserOperationRepository> { FirebaseDBUserOperationRepositoryImp(get()) }
        single<RadioAnalyticsOperationRepository> { RadioAnalyticsOperationRepositoryImp(get()) }
        single<FirebaseCommentOperationRepository> { FirebaseCommentOperationRepositoryImp(get()) }
        single<FirebaseUserRepository> { FirebaseUserRepositoryImp(get()) }
    }
}
