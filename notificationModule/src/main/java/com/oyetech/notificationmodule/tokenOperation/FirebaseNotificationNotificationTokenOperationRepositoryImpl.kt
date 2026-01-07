package com.oyetech.notificationmodule.tokenOperation

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.oyetech.domain.repository.firebase.FirebaseNotificationTokenOperationRepository
import com.oyetech.models.firebaseModels.firebaseToken.FirebaseNotificationTokenOperationModel
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

/**
Created by Erdi Özbek
-17.04.2025-
-14:40-
 **/

class FirebaseNotificationNotificationTokenOperationRepositoryImpl(private val firebaseMessaging: FirebaseMessaging) :
    FirebaseNotificationTokenOperationRepository {

    init {
        Timber.d("FirebaseTokenOperationRepositoryImpl initialized")
        getNotificationToken()
    }

    override val firebaseNotificationTokenStateFlow =
        MutableStateFlow<FirebaseNotificationTokenOperationModel?>(null)

    private fun getNotificationToken() {
        firebaseMessaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            firebaseNotificationTokenStateFlow.value = FirebaseNotificationTokenOperationModel(
                notificationToken = token,
            )
        })
    }
}
