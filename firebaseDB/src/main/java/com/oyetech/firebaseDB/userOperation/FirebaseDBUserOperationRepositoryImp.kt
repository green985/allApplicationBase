package com.oyetech.firebaseDB.userOperation

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseDBUserOperationRepository
import com.oyetech.firebaseDB.userOperation.databaseKey.FirebaseUserDatabaseKey
import com.oyetech.models.firebaseModels.userModel.FirebaseUserModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-19.06.2024-
-23:22-
 **/

class FirebaseDBUserOperationRepositoryImp() : FirebaseDBUserOperationRepository {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override val userDataStateFlow = MutableStateFlow<FirebaseUserModel?>(null)

    override fun saveUserOperation(userModel: FirebaseUserModel) {
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)

            .add(userModel)
            .addOnSuccessListener {
                userDataStateFlow.tryEmit(userModel)
            }
            .addOnFailureListener {
                // Handle failure
                userDataStateFlow.tryEmit(userModel)
            }
    }

    override fun saveUserOperationOrUpdate(userModel: FirebaseUserModel) {

        val collectionRef = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
        collectionRef.whereEqualTo("uid", userModel.uid).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Belirtilen ID'ye sahip doküman yoksa, yeni bir doküman ekle
                    collectionRef.add(userModel)
                        .addOnSuccessListener {
                            userDataStateFlow.tryEmit(userModel)
                            Log.d("Firestore", "DocumentSnapshot successfully written!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                "Firestore",
                                "Error writing document",
                                e
                            )
                        }
                } else {
                    // Belirtilen ID'ye sahip doküman varsa, güncelle
                    for (document in documents) {
                        collectionRef.document(document.id).set(userModel)
                            .addOnSuccessListener {
                                userDataStateFlow.tryEmit(userModel)
                                Log.d(
                                    "Firestore",
                                    "DocumentSnapshot successfully updated!"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "Firestore",
                                    "Error updating document",
                                    e
                                )
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting documents: ", e)
            }
    }

}