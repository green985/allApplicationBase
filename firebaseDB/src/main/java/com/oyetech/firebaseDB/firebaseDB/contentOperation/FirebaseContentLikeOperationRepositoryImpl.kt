package com.oyetech.firebaseDB.firebaseDB.contentOperation

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import com.oyetech.models.newPackages.helpers.OperationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
Created by Erdi Özbek
-13.02.2025-
-23:12-
 **/

class FirebaseContentLikeOperationRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val userRepository: FirebaseUserRepository,
) : FirebaseContentLikeOperationRepository {

    override fun getLikeListWithContentId(contentId: String): Flow<List<LikeOperationModel>> {
        return flow {

            val result = firestore.collection(FirebaseDatabaseKeys.likeOperationTable)
                .document(contentId)
                .collection("likes")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()

            if (result.size() == 0) {
                Timber.d("No likes found")
                emit(emptyList())
                return@flow
            }

            var wrapperResult = result.toObjects(LikeOperationModel::class.java)
            wrapperResult = result.mapIndexed { index, queryDocumentSnapshot ->
                wrapperResult[index].copy(likeId = queryDocumentSnapshot.id)
            }
            emit(wrapperResult)
        }

    }

    @Suppress("TooGenericExceptionThrown")
    override fun likeOperation(
        contentId: String,
    ): Flow<OperationState<LikeOperationModel>> = flow {
        try {
            val username = userRepository.getUsername() ?: ""

            if (username.isBlank()) {
                throw Exception(LanguageKey.usernameIsEmpty)
            }

            val userOldInput = firestore.collection(FirebaseDatabaseKeys.likeOperationTable)
                .document(contentId)
                .collection("likes")
                .document(username).get().await()

            var userOldInputResult = userOldInput.toObject(LikeOperationModel::class.java)

            if (userOldInputResult == null) {
                Timber.d("User not found")
                userOldInputResult =
                    LikeOperationModel(contentId = contentId, username = username, isLiked = false)
            }

            userOldInputResult = userOldInputResult.copy(isLiked = userOldInputResult.isLiked)

            val documentReference =
                firestore.runTransactionWithTimeout() { transaction ->
                    val commentRef = firestore.collection(FirebaseDatabaseKeys.likeOperationTable)
                        .document(contentId)
                        .collection("likes")
                        .document()


                    transaction.update(commentRef, username, userOldInputResult)
                    commentRef
                }

            Timber.d("Like added: ${documentReference.id}")
            emit(OperationState.Success(userOldInputResult))
        } catch (e: Exception) {
            Log.w("Firestore", "Error adding comment", e)
            emit(OperationState.Error(e))
        }
    }

    override fun getLikeCount(contentId: String): Flow<Int> {
        return flow {
            val result = firestore.collection(FirebaseDatabaseKeys.likeOperationTable)
                .document(contentId)
                .collection("likes")
                .get().await()

            emit(result.size())
        }
    }


}
