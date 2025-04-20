package com.oyetech.notificationmodule.tokenOperation

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.oyetech.domain.repository.firebase.FirebaseTokenOperationRepository
import com.oyetech.models.firebaseModels.firebaseToken.FirebaseTokenOperationModel
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

/**
Created by Erdi Özbek
-17.04.2025-
-14:40-
 **/

class FirebaseTokenOperationRepositoryImpl(private val firebaseMessaging: FirebaseMessaging) :
    FirebaseTokenOperationRepository {

    init {
        Timber.d("FirebaseTokenOperationRepositoryImpl initialized")
        getToken()
    }

    override val firebaseTokenStateFlow = MutableStateFlow<FirebaseTokenOperationModel?>(null)

    private fun getToken() {
        firebaseMessaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Timber.d("Firebase token: $token")
            firebaseTokenStateFlow.value = FirebaseTokenOperationModel(
                notificationToken = token,
            )
        })
    }
}
