package com.oyetech.firebaseDB.firebaseDB.contentOperation

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseContentLikeOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.contentOperationModel.LikeOperationModel
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
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

    override suspend fun getInitialStateOfContent(contentId: String): Flow<LikeOperationModel> {
        return flow<LikeOperationModel> {
            val username = userRepository.getUsername() ?: ""

            if (username.isBlank()) {
                emit(
                    LikeOperationModel(
                        contentId = contentId,
                    )
                )
                return@flow
            }

            val likeOperationModel = firestore.collection(FirebaseDatabaseKeys.likeOperationTable)
                .document(contentId)
                .collection("likes")
                .document(username).get().await()

            val userOldInputResult = likeOperationModel.toObject(LikeOperationModel::class.java)

            if (userOldInputResult == null) {
                emit(
                    LikeOperationModel(
                        contentId = contentId,
                    )
                )
            } else {
                emit(userOldInputResult)
            }
        }
    }

    @Suppress("TooGenericExceptionThrown")
    override suspend fun likeOperation(
        contentId: String,
    ): Flow<LikeOperationModel> = flow {
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
                LikeOperationModel(contentId = contentId, username = username, like = true)
            val documentReference =
                firestore.runTransactionWithTimeout() { transaction ->
                    val commentRef =
                        firestore.collection(FirebaseDatabaseKeys.likeOperationTable)
                            .document(contentId)
                            .collection("likes")
                            .document(username)

                    transaction.set(commentRef, userOldInputResult!!)
                    commentRef
                }

            Timber.d("Like added: NEW return fav = " + userOldInputResult.like)
            emit(userOldInputResult)
        } else {
            userOldInputResult = userOldInputResult.copy(like = !userOldInputResult.like)

            val documentReference =
                firestore.runTransactionWithTimeout() { transaction ->
                    val commentRef =
                        firestore.collection(FirebaseDatabaseKeys.likeOperationTable)
                            .document(contentId)
                            .collection("likes")
                            .document(username)

                    transaction.update(commentRef, "like", userOldInputResult.like)
                    commentRef
                }

            Timber.d("Like added: Update return fav = " + userOldInputResult.like)
            emit(userOldInputResult)
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
