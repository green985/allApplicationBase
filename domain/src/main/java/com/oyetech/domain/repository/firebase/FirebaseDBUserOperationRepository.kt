package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.userModel.FirebaseUserModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-19.06.2024-
-23:34-
 **/

interface FirebaseDBUserOperationRepository {
    val userDataStateFlow: MutableStateFlow<FirebaseUserModel?>
    fun saveUserOperation(userModel: FirebaseUserModel)
    fun saveUserOperationOrUpdate(userModel: FirebaseUserModel)
}
