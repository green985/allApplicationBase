package com.oyetech.firebaseDB.di

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.RadioAnalyticsOperationRepository
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import com.oyetech.firebaseDB.firebaseDB.FirebaseContactWithMeOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.comment.FirebaseCommentOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.helper.FirebaseOnlineHelper
import com.oyetech.firebaseDB.firebaseDB.quotes.FirebaseQuotesOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.radio.RadioAnalyticsOperationRepositoryImp
import com.oyetech.firebaseDB.userOperation.FirebaseUserRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-11.04.2023-
-18:37-
 **/

object FirebaseDBModule {

    var firebaseDBModulee = module {
        single<FirebaseFirestore> {
            FirebaseFirestore.setLoggingEnabled(true)

            val firestore = FirebaseFirestore.getInstance()

//            val settings = firestoreSettings {
//                isPersistenceEnabled = false
//            }
//            firestore.firestoreSettings = settings
            firestore
        }
        single<RadioAnalyticsOperationRepository> { RadioAnalyticsOperationRepositoryImp(get()) }

        single<FirebaseContactWithMeOperationRepository> {
            FirebaseContactWithMeOperationRepositoryImp(
                get()
            )
        }

        single<FirebaseCommentOperationRepository> {
            FirebaseCommentOperationRepositoryImp(
                get(),
                get()
            )
        }
        single<FirebaseUserRepository> { FirebaseUserRepositoryImp(get()) }
        single<FirebaseQuotesOperationRepository> { FirebaseQuotesOperationRepositoryImp(get()) }


        single { FirebaseOnlineHelper() }
    }
}
