package com.oyetech.firebaseDB.userOperation

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.userOperation.databaseKey.FirebaseUserDatabaseKey
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseUserRepositoryImp(
    private val firestore: FirebaseFirestore,
) : FirebaseUserRepository {

    override val userDataStateFlow = MutableStateFlow<FirebaseUserProfileModel?>(null)

    override suspend fun updateUserName(username: String) {
        var userData = userDataStateFlow.value
        if (userData == null) {
            userDataStateFlow.value = userDataStateFlow.value?.copy(
                errorException = Exception("User not found")
            )
            return
        }

        val isUsernameInUse = checkIsUsernameInUse(username).firstOrNull() ?: false
        Timber.d("isUsernameInUse: $isUsernameInUse")
        if (isUsernameInUse) {
            userDataStateFlow.value =
                userDataStateFlow.value?.copy(errorException = Exception("Username is already in use"))
            return
        }

        var newUserDataModel = userData.copy(username = username)

        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(userData.uid).set(
            newUserDataModel,
        ).addOnSuccessListener {
            newUserDataModel = newUserDataModel.copy(errorException = null)
            userDataStateFlow.value = newUserDataModel
        }.addOnFailureListener { exception ->
            userDataStateFlow.value = userDataStateFlow.value?.copy(errorException = exception)
        }

    }

    fun createProfile(user: FirebaseUserProfileModel) {
        userDataStateFlow.value = FirebaseUserProfileModel()
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                userDataStateFlow.value = user.copy()
            }
            .addOnFailureListener { exception ->
                userDataStateFlow.value =
                    FirebaseUserProfileModel(errorException = Exception(LanguageKey.createUserErrorMessage))

                println("Error creating user: ${exception.message}")
            }
    }

    suspend fun checkIsUsernameInUse(username: String): Flow<Boolean> {
        val result = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .whereEqualTo("username", username)
            .get().await()

        return flowOf(!result.isEmpty)
    }

    override fun deleteUser(uid: String) {
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .document(uid)
            .delete()
            .addOnSuccessListener {
                userDataStateFlow.value = FirebaseUserProfileModel(isUserDeleted = true)
            }
            .addOnFailureListener { exception ->
                userDataStateFlow.value =
                    userDataStateFlow.value?.copy(errorException = Exception(LanguageKey.deleteUserErrorMessage))
            }
    }

    override fun getUserProfileWithUid(firebaseProfileUserModel: FirebaseUserProfileModel) {
        val uid = firebaseProfileUserModel.uid
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(uid).get()
            .addOnSuccessListener {
                var userData = it.toObject(FirebaseUserProfileModel::class.java)

                userData = userData?.copy(
                    lastSignInTimestamp = firebaseProfileUserModel.lastSignInTimestamp,
                    isAnonymous = firebaseProfileUserModel.isAnonymous
                )

                if (userData != null && userData.uid == uid) {
                    userDataStateFlow.tryEmit(userData)
                } else {
                    createProfile(firebaseProfileUserModel)
                }

            }.addOnFailureListener {
                userDataStateFlow.value =
                    FirebaseUserProfileModel(errorException = it)
            }
    }

    override fun getUserProfileForAutoLogin(
        firebaseProfileUserModel: FirebaseUserProfileModel,
        afterAction: ((Boolean) -> Unit),
    ) {
        val uid = firebaseProfileUserModel.uid
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(uid).get()
            .addOnSuccessListener {
                var userData = it.toObject(FirebaseUserProfileModel::class.java)

                userData = userData?.copy(
                    lastSignInTimestamp = firebaseProfileUserModel.lastSignInTimestamp,
                    isAnonymous = firebaseProfileUserModel.isAnonymous
                )

                if (userData != null && userData.uid == uid) {
                    userDataStateFlow.value = (userData)
                    afterAction.invoke(true)
                }

            }.addOnFailureListener {
                userDataStateFlow.value =
                    FirebaseUserProfileModel(errorException = it)
                afterAction.invoke(true)
            }
    }

    override fun getUsername(): String {
        return userDataStateFlow.value?.username ?: ""
    }

    override fun isMyContent(contentUsername: String): Boolean {
        val username = getUsername()
        return username == contentUsername
    }

//
//    fun updateLastLogin(uid: String) {
//        val userDocRef = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(uid)
//
//        userDocRef.get()
//            .addOnSuccessListener { documentSnapshot ->
//                val currentUser = documentSnapshot.toObject(FirebaseUserProfileModel::class.java)
//                if (currentUser != null) {
//                        .addOnSuccessListener {
//                            userDataStateFlow.tryEmit(updatedUser)
//                        }
//                        .addOnFailureListener { exception ->
//                            println("Error updating user: ${exception.message}")
//                        }
//                } else {
//                    println("User not found!")
//                }
//            }
//            .addOnFailureListener { exception ->
//                println("Error fetching user: ${exception.message}")
//            }
//    }
}
