package com.oyetech.firebaseDB.userOperation

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.userOperation.databaseKey.FirebaseUserDatabaseKey
import com.oyetech.models.firebaseModels.userModel.FirebaseUserModel
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.coroutines.flow.MutableStateFlow

class FirebaseUserRepositoryImp(
    private val firestore: FirebaseFirestore,
) : FirebaseUserRepository {

    override val userDataStateFlow = MutableStateFlow<FirebaseUserModel?>(null)

    override fun createProfile(user: FirebaseUserModel) {
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                userDataStateFlow.tryEmit(user)
            }
            .addOnFailureListener { exception ->
                println("Error creating user: ${exception.message}")
            }
    }

    override fun checkUsername(username: String): Boolean {
        var isAvailable = false
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                isAvailable = querySnapshot.isEmpty
            }
            .addOnFailureListener { exception ->
                println("Error checking username: ${exception.message}")
            }
        return isAvailable
    }

    override fun deleteUser(uid: String) {
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .document(uid)
            .delete()
            .addOnSuccessListener {
                userDataStateFlow.tryEmit(null)
            }
            .addOnFailureListener { exception ->
                println("Error deleting user: ${exception.message}")
            }
    }

    override fun updateLastLogin(uid: String) {
        val userDocRef = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(uid)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val currentUser = documentSnapshot.toObject(FirebaseUserModel::class.java)

                if (currentUser != null) {
                    val lastLoginDate =
                        TimeFunctions.getFullDateFromLongMilis(System.currentTimeMillis())
                    val updatedUser = currentUser.copy(lastLoginDateString = lastLoginDate)

                    userDocRef.set(updatedUser)
                        .addOnSuccessListener {
                            userDataStateFlow.tryEmit(updatedUser)
                        }
                        .addOnFailureListener { exception ->
                            println("Error updating user: ${exception.message}")
                        }
                } else {
                    println("User not found!")
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching user: ${exception.message}")
            }
    }
}
