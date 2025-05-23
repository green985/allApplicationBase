package com.oyetech.firebaseDB.userOperation

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.userOperation.databaseKey.FirebaseUserDatabaseKey
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
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

    override suspend fun updateUserProperty(userData: FirebaseUserProfileModel) {
        val isUsernameInUse = checkIsUsernameInUse(userData.username).firstOrNull() ?: false
        Timber.d("isUsernameInUse: $isUsernameInUse")
        if (isUsernameInUse) {
            userDataStateFlow.value =
                userDataStateFlow.value?.copy(errorException = Exception("Username is already in use"))
            return
        }

        var newUserDataModel = userData

        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(userData.userId).set(
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
            .document(user.userId)
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
        try {
            firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
                .document(uid)
                .delete()
                .addOnSuccessListener {

                }
                .addOnFailureListener { exception ->
                    userDataStateFlow.value =
                        userDataStateFlow.value?.copy(errorException = Exception(LanguageKey.deleteUserErrorMessage))
                }
        } catch (e: Exception) {

        }

    }

    override fun getUserProfileWithUid(firebaseProfileUserModel: FirebaseUserProfileModel) {
        val uid = firebaseProfileUserModel.userId
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(uid).get()
            .addOnSuccessListener {
                var userData = it.toObject(FirebaseUserProfileModel::class.java)

                userData = userData?.copy(
                    lastSignInTimestamp = firebaseProfileUserModel.lastSignInTimestamp,
                    isAnonymous = firebaseProfileUserModel.isAnonymous
                )

                if (userData != null && userData.userId == uid) {
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
        val uid = firebaseProfileUserModel.userId
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION).document(uid).get()
            .addOnSuccessListener {
                var userData = it.toObject(FirebaseUserProfileModel::class.java)

                userData = userData?.copy(
                    lastSignInTimestamp = firebaseProfileUserModel.lastSignInTimestamp,
                    isAnonymous = firebaseProfileUserModel.isAnonymous
                )

                if (userData != null && userData.userId == uid) {
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

    override fun getUserId(): String {
        return userDataStateFlow.value?.userId ?: ""
    }

    override fun getUserProfileModel(): MutableStateFlow<FirebaseUserProfileModel?> {
        return userDataStateFlow
    }

    override fun isMyContent(contentUsername: String): Boolean {
        val username = getUsername()
        return username == contentUsername
    }

    override fun updateUserNotificationToken(notificationToken: String) {
        val userId = getUserId()
        val userDocRef =
            firestore.collection(FirebaseDatabaseKeys.userList)
                .document(FirebaseDatabaseKeys.generalUserList)
                .collection("users").document(userId)

        userDocRef.update("notificationToken", notificationToken)
            .addOnSuccessListener {
                Timber.d("Notification token updated successfully")
            }
            .addOnFailureListener { exception ->
                Timber.e("Error updating notification token: ${exception.message}")
            }
    }

}
