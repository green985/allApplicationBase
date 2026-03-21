package com.oyetech.firebaseDB.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseLanguageOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseMessagingRepository
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseQuestionTagRepository
import com.oyetech.domain.repository.firebase.FirebaseStorageRepository
import com.oyetech.domain.repository.firebase.FirebaseUserListOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserPropertyRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.domain.repository.firebase.RadioAnalyticsOperationRepository
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import com.oyetech.firebaseDB.files.FirebaseStorageRepositoryImpl
import com.oyetech.firebaseDB.firebaseDB.FirebaseContactWithMeOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.comment.FirebaseCommentOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.contentOperation.FirebaseContentLikeOperationRepositoryImpl
import com.oyetech.firebaseDB.firebaseDB.helper.FirebaseOnlineHelper
import com.oyetech.firebaseDB.firebaseDB.language.FirebaseLanguageOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.messaging.FirebaseMessagingRepositoryImpl
import com.oyetech.firebaseDB.firebaseDB.question.FirebaseQuestionOperationRepositoryImpl
import com.oyetech.firebaseDB.firebaseDB.question.FirebaseQuestionTagRepositoryImpl
import com.oyetech.firebaseDB.firebaseDB.radio.RadioAnalyticsOperationRepositoryImp
import com.oyetech.firebaseDB.firebaseDB.userList.FirebaseUserListOperationRepositoryImpl
import com.oyetech.firebaseDB.userOperation.FirebaseUserPropertyRepositoryImpl
import com.oyetech.firebaseDB.userOperation.FirebaseUserRepositoryImp
import org.koin.dsl.module

/**
Created by Erdi Özbek
-11.04.2023-
-18:37-
 **/

object FirebaseDBModule {

    var firebaseDBModulee = module {

        single<FirebaseStorage> {
//            FirebaseFirestore.setLoggingEnabled(true)

            val firestore = FirebaseStorage.getInstance()

//            val settings = firestoreSettings {
//                isPersistenceEnabled = false
//            }
//            firestore.firestoreSettings = settings
            firestore
        }
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
        single<FirebaseUserPropertyRepository> { FirebaseUserPropertyRepositoryImpl(get()) }

        single<FirebaseLanguageOperationRepository> { FirebaseLanguageOperationRepositoryImp(get()) }
        single<FirebaseMessagingRepository> {
            FirebaseMessagingRepositoryImpl(
                firestore = get(),
                userRepository = get(),
                messagesSendingOperationRepository = get(),
                messagesAllOperationRepository = get(),
                dispatcher = get(),
                activityProviderUseCase = get(),
                firebaseRealtimeHelperRepository = get(),
                firebaseCloudOperationRepository = get()
            )
        }

        single<FirebaseQuestionOperationRepository> {
            FirebaseQuestionOperationRepositoryImpl(
                get()
            )
        }

        single<FirebaseQuestionTagRepository> {
            FirebaseQuestionTagRepositoryImpl(get())
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

        single<FirebaseStorageRepository> {
            FirebaseStorageRepositoryImpl(
                get()
            )
        }
    }
}
