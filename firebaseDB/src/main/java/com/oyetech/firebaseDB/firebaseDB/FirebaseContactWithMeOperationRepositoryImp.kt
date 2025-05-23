package com.oyetech.firebaseDB.firebaseDB

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.postBody.feedback.FeedbackOperationRequestBody
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-04/04/2024-
-23:43-
 **/

class FirebaseContactWithMeOperationRepositoryImp(private val firestore: FirebaseFirestore) :
    FirebaseContactWithMeOperationRepository {

    override val sendFeedbackOperationStateFlow = MutableStateFlow<Boolean?>(null)

    override fun sendFeedback(email: String, message: String, name: String) {
        val feedbackOperationResponseBody = FeedbackOperationRequestBody(
            email = email,
            name = name.take(100),
            message = message.take(500)
        )

        firestore.collection(FirebaseDatabaseKeys.feedbackCollection)
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
