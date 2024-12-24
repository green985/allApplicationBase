package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-19.06.2024-
-23:34-
 **/

interface FirebaseDBUserOperationRepository {
    val userDataStateFlow: MutableStateFlow<FirebaseUserProfileModel?>
    fun saveUserOperation(userModel: FirebaseUserProfileModel)
    fun saveUserOperationOrUpdate(userModel: FirebaseUserProfileModel)
}
