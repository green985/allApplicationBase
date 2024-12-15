package com.oyetech.domain.repository.helpers

import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-07/04/2024-
-16:05-
 **/

interface FirebaseContactWithMeOperationRepository {

    val sendFeedbackOperationStateFlow: MutableStateFlow<Boolean?>
    fun sendFeedback(email: String, message: String, name: String)
    fun clearFeedbackOperationStateFlow()
}