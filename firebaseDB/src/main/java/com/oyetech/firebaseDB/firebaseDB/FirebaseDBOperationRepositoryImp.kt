package com.oyetech.firebaseDB.firebaseDB

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.postBody.feedback.FeedbackOperationRequestBody
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-04/04/2024-
-23:43-
 **/

class FirebaseDBOperationRepositoryImp :
    FirebaseContactWithMeOperationRepository {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override val sendFeedbackOperationStateFlow = MutableStateFlow<Boolean?>(null)

    override fun sendFeedback(email: String, message: String, name: String) {
        val feedbackOperationResponseBody = FeedbackOperationRequestBody(
            email = email,
            name = name.take(100),
            message = message.take(500)
        )

        firestore.collection(FirebaseDatabaseKeys.RADIO_FEEDBACK_COLLECTION)
            .add(feedbackOperationResponseBody)
            .addOnSuccessListener {
                sendFeedbackOperationStateFlow.tryEmit(true)
            }
            .addOnFailureListener {
                // Handle failure
                sendFeedbackOperationStateFlow.tryEmit(false)
            }
    }

    override fun clearFeedbackOperationStateFlow() {
        sendFeedbackOperationStateFlow.tryEmit(null)
    }

}
