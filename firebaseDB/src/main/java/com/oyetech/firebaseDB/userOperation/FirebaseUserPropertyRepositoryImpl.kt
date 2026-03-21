package com.oyetech.firebaseDB.userOperation

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseUserPropertyRepository
import com.oyetech.firebaseDB.firebaseDB.helper.runTransactionWithTimeout
import com.oyetech.firebaseDB.userOperation.databaseKey.FirebaseUserDatabaseKey
import timber.log.Timber

class FirebaseUserPropertyRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseUserPropertyRepository {

    override val updateOperationSharedEvent =
        kotlinx.coroutines.flow.MutableSharedFlow<Unit>()

    override suspend fun updateBiography(userId: String, biography: String): Result<Unit> {
        return try {
            val documentReference = firestore.runTransactionWithTimeout { transaction ->
                val collection = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
                    .document(userId)
                    .collection("userProperty").document("biography")

                transaction.set(collection, mapOf("biography" to biography))
                collection
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Timber.e("Error updating biography: ${exception.message}")
            Result.failure(exception)
        }
    }
}
