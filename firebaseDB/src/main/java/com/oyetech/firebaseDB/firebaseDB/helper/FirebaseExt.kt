package com.oyetech.firebaseDB.firebaseDB.helper

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.oyetech.models.utils.const.HelperConstant
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

suspend fun <T> FirebaseFirestore.runTransactionWithTimeout(
    timeoutMillis: Long = HelperConstant.FirebaseDB_TRANSACTION_TIMEOUT,
    transactionBody: (Transaction) -> T,
): T {
    return withTimeout(timeoutMillis) {
        this@runTransactionWithTimeout.runTransaction { transaction ->
            transactionBody(transaction)
        }.await()
    }
}