package com.oyetech.firebaseDB.di

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseLanguageOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseQuotesDebugOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserListOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.RadioAnalyticsOperationRepository
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import com.oyetech.firebaseDB.firebaseDB.FirebaseContactWithMeOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.comment.FirebaseCommentOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.contentOperation.FirebaseContentLikeOperationRepositoryImpl
import com.oyetech.firebaseDB.firebaseDB.helper.FirebaseOnlineHelper
import com.oyetech.firebaseDB.firebaseDB.language.FirebaseLanguageOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.messaging.FirebaseMessagingRepositoryImpl
import com.oyetech.firebaseDB.firebaseDB.quotes.FirebaseQuotesOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.radio.RadioAnalyticsOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.userList.FirebaseUserListOperationRepositoryImpl
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
//            FirebaseFirestore.setLoggingEnabled(true)

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

        single<FirebaseQuotesOperationRepository> {
            FirebaseQuotesOperationRepositoryImp(
                get(),
                get(),
            )
        }

        single<FirebaseQuotesDebugOperationRepository> {
            FirebaseQuotesOperationRepositoryImp(
                get(),
                get(),
            )
        }
        single<FirebaseLanguageOperationRepository> { FirebaseLanguageOperationRepositoryImp(get()) }
        single<FirebaseMessagingRepository> {
            FirebaseMessagingRepositoryImpl(
                get(),
                get(),
                get(),
                get(), get(), get(), get(), get()
            )
        }


        single { FirebaseOnlineHelper() }
        single<FirebaseContentLikeOperationRepository> {
            FirebaseContentLikeOperationRepositoryImpl(
                get(), get()
            )
        }

        single<FirebaseUserListOperationRepository> {
            FirebaseUserListOperationRepositoryImpl(
                get(), get()
            )
        }

    }
}
