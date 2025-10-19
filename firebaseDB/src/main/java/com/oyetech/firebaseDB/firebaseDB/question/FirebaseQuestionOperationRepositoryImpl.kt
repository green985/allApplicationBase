package com.oyetech.firebaseDB.firebaseDB.question

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Transactional Firestore operations for Questions.
 */
class FirebaseQuestionOperationRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseQuestionOperationRepository {

    @Suppress("TooGenericExceptionThrown")
    override fun createQuestion(body: QuestionOperationResponseBody): Flow<Unit> = flow {
        try {
            val documentReference = firestore.runTransactionWithTimeout { transaction ->
                val collection = firestore.collection(FirebaseDatabaseKeys.createQuestion)
                val docRef =
                    if (body.questionId.isBlank()) collection.document() else collection.document(
                        body.questionId
                    )
                transaction.set(docRef, body)
                docRef
            }
            Timber.d("Question created: ${documentReference.id}")
            emit(Unit)
        } catch (e: Exception) {
            error(GeneralException(e.message ?: "Question create error"))
        }
    }

    override fun getQuestionList(): Flow<List<QuestionOperationResponseBody>> = flow {
        try {
            val snapshot = firestore
                .collection(FirebaseDatabaseKeys.createQuestion)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val questionList = snapshot.documents.mapNotNull { doc ->
                val docc = doc.toObject(QuestionOperationResponseBody::class.java)
                docc?.copy(questionId = doc.id)
            }
            emit(questionList)
        } catch (e: Exception) {
            error(GeneralException(e.message ?: "Question list fetch error"))
        }
    }

    override fun getQuestionsFiltered(
        moderationStatus: ModerationStatus?,
        tag: QueTag?,
    ): Flow<List<QuestionOperationResponseBody>> = flow {
        try {
            var query: Query = firestore
                .collection(FirebaseDatabaseKeys.createQuestion)

            val status = moderationStatus
            // Apply moderation filter if explicitly provided or defaulted
            if (status != null && status != ModerationStatus.ALL) {
                query = query.whereEqualTo("moderationStatus", status.name)
            }

            // Apply tag filter if provided (array-contains requires full object equality)
            if (tag != null) {
                query = query.whereArrayContains("tags", tag)
            }

            // Order by createdAt desc
            query = query.orderBy("createdAt", Query.Direction.DESCENDING)

            val snapshot = query.get().await()

            val questionList = snapshot.documents.mapNotNull { doc ->
                val obj = doc.toObject(QuestionOperationResponseBody::class.java)
                obj?.copy(questionId = doc.id)
            }
            Timber.d("Filtered questions fetched: ${questionList.size} items")
            Timber.d("Filters - ModerationStatus: ${status?.name}, Tag: ${tag?.name ?: "None"}")
            emit(questionList)
        } catch (e: Exception) {
            error(GeneralException(e.message ?: "Question filtered list fetch error"))
        }
    }

    override fun getQuestionById(questionId: String): Flow<QuestionOperationResponseBody> = flow {
        try {
            val snapshot = firestore
                .collection(FirebaseDatabaseKeys.createQuestion)
                .document(questionId)
                .get()
                .await()

            val question = snapshot.toObject(QuestionOperationResponseBody::class.java)
                ?: error(GeneralException("Question not found"))

            emit(question.copy(questionId = snapshot.id))
        } catch (e: Exception) {
            error(GeneralException(e.message ?: "Question fetch error"))
        }
    }

    override fun updateQuestionStatus(
        questionId: String,
        status: ModerationStatus,
    ): Flow<Unit> = flow {
        try {
            firestore
                .collection(FirebaseDatabaseKeys.createQuestion)
                .document(questionId)
                .update(
                    mapOf(
                        "moderationStatus" to status.name,
                    )
                )
                .await()
            emit(Unit)
        } catch (e: Exception) {
            error(GeneralException(e.message ?: "Question status update error"))
        }
    }

    override fun updateQuestion(body: QuestionOperationResponseBody): Flow<Unit> = flow {
        try {
            firestore.runTransactionWithTimeout { transaction ->
                val docRef = firestore
                    .collection(FirebaseDatabaseKeys.createQuestion)
                    .document(body.questionId)
                transaction.set(docRef, body)
            }
            Timber.d("Question updated: ${body.questionId}")
            emit(Unit)
        } catch (e: Exception) {
            error(GeneralException(e.message ?: "Question update error"))
        }
    }
}
