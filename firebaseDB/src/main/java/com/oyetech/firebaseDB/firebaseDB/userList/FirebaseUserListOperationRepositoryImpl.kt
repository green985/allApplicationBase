package com.oyetech.firebaseDB.firebaseDB.userList

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.oyetech.domain.repository.firebase.FirebaseUserListOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.userList.FirebaseUserListModel
import com.oyetech.models.firebaseModels.userList.toMapFirebaseUserListModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
Created by Erdi Özbek
-26.02.2025-
-20:17-
 **/

class FirebaseUserListOperationRepositoryImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseUserRepository: FirebaseUserRepository,
) : FirebaseUserListOperationRepository {

    private val visibleUserIdList = arrayListOf<String>()

    override fun setVisibleByUserId(userId: String) {
        visibleUserIdList.add(userId)
    }

    override fun getRandomUsersFromDatabase(): Flow<List<FirebaseUserListModel>> {
        val userId = firebaseUserRepository.getUserId()
        return flow<List<FirebaseUserListModel>> {
            val result =
                firebaseFirestore.collection(FirebaseDatabaseKeys.userList)
                    .document(FirebaseDatabaseKeys.generalUserList)
                    .collection("users").orderBy("joinedAt", Query.Direction.DESCENDING)
                    .limit(100)
                    .get()
                    .await()

            var documentIdList = arrayListOf<String>()
            var resultList = result.mapNotNull {
                documentIdList.add(it.id)
                it.toObject(FirebaseUserListModel::class.java)
            }

            resultList = resultList.filter {
                it.userId != userId
            }

            emit(resultList)

        }

    }

    suspend fun getRandomUsersFromRoom(roomId: String): List<Map<String, Any>> {
        val db = FirebaseFirestore.getInstance()
        val usersRef =
            db.collection(FirebaseDatabaseKeys.generalUserList).document(roomId).collection("users")

        return try {
            val snapshot = usersRef
                .orderBy("joinedAt", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .await()

            val userList = snapshot.documents.map { it.data!! }

            // Rastgele 20 kullanıcı seç
            userList.shuffled().take(20)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addUserToUserList(): Flow<Unit> {
        return flow<Unit> {
            val userProfileModel = firebaseUserRepository.getUserProfileModel()
            if (userProfileModel == null) {
                throw GeneralException("User profile model is null")
            }
            val userId = userProfileModel.userId
            val username = firebaseUserRepository.getUsername()
            if (userId.isBlank() || username.isBlank()) {
                throw GeneralException("User id is null or blank")
            }
            val userRef =
                firebaseFirestore.collection(FirebaseDatabaseKeys.userList)
                    .document(FirebaseDatabaseKeys.generalUserList)
                    .collection("users").document(userId)

            val data = userProfileModel.toMapFirebaseUserListModel()

            val result = firebaseFirestore.runTransactionWithTimeout {
                userRef.set(data, SetOptions.merge())
                data
            }

            Timber.d("User added to userList: $result")

            emit(Unit)
        }


    }
}
