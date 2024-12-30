package com.oyetech.firebaseDB.firebaseDB.comment

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.commentModel.CommentResponseData
import com.oyetech.models.newPackages.helpers.OperationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
Created by Erdi Özbek
-20.12.2024-
-22:13-
 **/

class FirebaseCommentOperationRepositoryImp(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
) :
    FirebaseCommentOperationRepository {

    override suspend fun getCommentsWithId(commentId: String): Flow<List<CommentResponseData>> {

        val result = firestore.collection(FirebaseDatabaseKeys.commentTable)
            .document(commentId)
            .collection("comments")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await()

        val creationResultList = result.map {
            it.getTimestamp("createdAt")
        }
        Timber.d("creationResultList: $creationResultList")

        if (result.size() == 0) {
            Timber.d("No comments found")
            return flowOf(emptyList())
        }

        val wrapperResult = result.toObjects(CommentResponseData::class.java)
        return flowOf(wrapperResult)

    }

    @Suppress("TooGenericExceptionThrown")
    override fun addCommentFlow(
        contentId: String,
        content: String,
    ): Flow<OperationState<Boolean>> = flow {
        emit(OperationState.Loading)
        try {
            val username = userRepository.getUsername() ?: ""

            if (username.isBlank()) {
                throw Exception(LanguageKey.usernameIsEmpty)
            }

            val comment =
                CommentResponseData(contentId = contentId, content = content, username = username)

            val documentReference =
                firestore.runTransactionWithTimeout() { transaction ->
                    val commentRef = firestore.collection(FirebaseDatabaseKeys.commentTable)
                        .document(contentId)
                        .collection("comments")
                        .document()

                    transaction.set(commentRef, comment)
                    commentRef
                }

            Timber.d("Comment added: ${documentReference.id}")
            emit(OperationState.Success(true))
        } catch (e: Exception) {
            Log.w("Firestore", "Error adding comment", e)
            emit(OperationState.Error(e))
        }
    }
}
