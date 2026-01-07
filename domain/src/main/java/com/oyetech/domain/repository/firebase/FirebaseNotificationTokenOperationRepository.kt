package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.firebaseToken.FirebaseNotificationTokenOperationModel
import kotlinx.coroutines.flow.MutableStateFlow

interface FirebaseNotificationTokenOperationRepository {
    val firebaseNotificationTokenStateFlow: MutableStateFlow<FirebaseNotificationTokenOperationModel?>
}
