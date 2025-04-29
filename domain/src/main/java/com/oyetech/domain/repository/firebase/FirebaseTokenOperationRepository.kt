package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.firebaseToken.FirebaseTokenOperationModel
import kotlinx.coroutines.flow.MutableStateFlow

interface FirebaseTokenOperationRepository {
    val firebaseTokenStateFlow: MutableStateFlow<FirebaseTokenOperationModel?>
}
