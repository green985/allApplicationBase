package com.oyetech.firebaseDB.firebaseDB.question

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.oyetech.domain.repository.firebase.FirebaseQuestionOperationRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionApprovedListType
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
            e.printStackTrace()
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

    override fun getApprovedQuestionsSorted(
        type: QuestionApprovedListType,
        limit: Int,
    ): Flow<List<QuestionOperationResponseBody>> = flow {
        try {
            when (type) {
                QuestionApprovedListType.ALL, QuestionApprovedListType.RECENTLY_ADDED -> {
                    var query: Query = firestore
                        .collection(FirebaseDatabaseKeys.createQuestion)
                        .whereEqualTo("moderationStatus", ModerationStatus.APPROVED.name)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                    if (limit > 0) query = query.limit(limit.toLong())
                    val snapshot = query.get().await()
                    val items = snapshot.documents.mapNotNull { d ->
                        d.toObject(QuestionOperationResponseBody::class.java)
                            ?.copy(questionId = d.id)
                    }
                    emit(items)
                }

                QuestionApprovedListType.RECENTLY_ANSWERED -> {
                    emit(getMostRecentlyAnsweredInternal(limit))
                }
            }
        } catch (e: Exception) {
            error(GeneralException(e.message ?: "Approved questions fetch error"))
        }
    }

    override fun getMostRecentlyAnsweredQuestions(limit: Int): Flow<List<QuestionOperationResponseBody>> =
        flow {
            try {
                emit(getMostRecentlyAnsweredInternal(limit))
            } catch (e: Exception) {
                error(GeneralException(e.message ?: "Most recently answered fetch error"))
            }
        }

    private suspend fun getMostRecentlyAnsweredInternal(limit: Int): List<QuestionOperationResponseBody> {
        // 1) Fetch newest answers globally
        val answersSnap = firestore
            .collectionGroup("answers")
            .orderBy("submittedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong().coerceAtLeast(10L))
            .get()
            .await()

        // 2) Distinct questionIds preserving order
        val questionIdsOrdered = answersSnap.documents
            .mapNotNull { it.getString("questionId") }
            .distinct()
            .take(limit)

        if (questionIdsOrdered.isEmpty()) return emptyList()

        // 3) Fetch questions by id batches of 10 (whereIn limit)
        val result = mutableListOf<QuestionOperationResponseBody>()
        questionIdsOrdered.chunked(10).forEach { batch ->
            val snap = firestore
                .collection(FirebaseDatabaseKeys.createQuestion)
                .whereIn(FieldPath.documentId(), batch)
                .get()
                .await()
            val items = snap.documents.mapNotNull { d ->
                d.toObject(QuestionOperationResponseBody::class.java)?.copy(questionId = d.id)
            }
            // filter only approved
            result += items.filter { it.moderationStatus == ModerationStatus.APPROVED }
        }

        // 4) Sort result by latest answer order
        val orderIndex = questionIdsOrdered.withIndex().associate { it.value to it.index }
        return result.sortedBy { orderIndex[it.questionId] ?: Int.MAX_VALUE }
    }

    override fun getUserQuestions(userId: String): Flow<List<QuestionOperationResponseBody>> =
        flow {
            try {
                val snapshot = firestore
                    .collection(FirebaseDatabaseKeys.createQuestion)
                    .whereEqualTo("createdBy", userId)
                    .whereEqualTo("moderationStatus", "APPROVED")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val questionList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(QuestionOperationResponseBody::class.java)
                        ?.copy(questionId = doc.id)
                }
                Timber.d("User questions fetched: ${questionList.size} items for userId: $userId")
                emit(questionList)
            } catch (e: Exception) {
                error(GeneralException(e.message ?: "User questions fetch error"))
            }
        }

    override fun getUserAnsweredQuestions(userId: String): Flow<List<QuestionOperationResponseBody>> =
        flow {
            try {
                // 1) Fetch answers by userId
                val answersSnap = firestore
                    .collectionGroup("answers")
                    .whereEqualTo("userId", userId)
                    .orderBy("submittedAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                // 2) Extract distinct questionIds
                val questionIds = answersSnap.documents
                    .mapNotNull { it.getString("questionId") }
                    .distinct()

                if (questionIds.isEmpty()) {
                    emit(emptyList())
                    return@flow
                }

                // 3) Fetch questions by id batches of 10
                val result = mutableListOf<QuestionOperationResponseBody>()
                questionIds.chunked(10).forEach { batch ->
                    val snap = firestore
                        .collection(FirebaseDatabaseKeys.createQuestion)
                        .whereIn(FieldPath.documentId(), batch)
                        .get()
                        .await()
                    val items = snap.documents.mapNotNull { d ->
                        d.toObject(QuestionOperationResponseBody::class.java)
                            ?.copy(questionId = d.id)
                    }
                    result += items
                }

                // 4) Sort by answer submission order
                val orderIndex = questionIds.withIndex().associate { it.value to it.index }
                val sorted = result.sortedBy { orderIndex[it.questionId] ?: Int.MAX_VALUE }
                Timber.d("User answered questions fetched: ${sorted.size} items for userId: $userId")
                emit(sorted)
            } catch (e: Exception) {
                error(GeneralException(e.message ?: "User answered questions fetch error"))
            }
        }
}
