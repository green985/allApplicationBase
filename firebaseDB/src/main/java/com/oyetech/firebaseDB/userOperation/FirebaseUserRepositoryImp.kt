package com.oyetech.firebaseDB.userOperation

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.firebaseDB.userOperation.databaseKey.FirebaseUserDatabaseKey
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.errors.exceptionHelper.GeneralException
import com.oyetech.models.firebaseModels.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.userModel.FirebaseUserProfileModel
import com.oyetech.models.firebaseModels.userModel.FirebaseUserPropertyModel
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseUserRepositoryImp(
    private val firestore: FirebaseFirestore,
) : FirebaseUserRepository {

    override val userProfileDataStateFlow =
        MutableStateFlow<UserProfileProperty>(UserProfileProperty())

    override val userDataStateFlow =
        MutableStateFlow<FirebaseUserProfileModel>(FirebaseUserProfileModel())

    override suspend fun updateUserProperty(userData: FirebaseUserProfileModel) {
        try {
            // todo will be handle...
//            val inUse = isUsernameInUse(username = userData.username)
//            if (inUse) {
//                userDataStateFlow.value =
//                    FirebaseUserProfileModel(errorException = Exception("Username is already in use"))
//                return
//            }

            val toSave = userData.copy(errorException = null)
            firestore
                .collection(FirebaseUserDatabaseKey.USER_COLLECTION)
                .document(userData.userId)
                .set(toSave)
                .addOnSuccessListener {
                    userDataStateFlow.value = toSave
                }
                .addOnFailureListener { ex ->
                    userDataStateFlow.value = FirebaseUserProfileModel(errorException = ex)
                }
        } catch (e: Exception) {
            userDataStateFlow.value = FirebaseUserProfileModel(errorException = e)
        }
    }

    fun createProfile(user: FirebaseUserProfileModel) {
        userDataStateFlow.value = FirebaseUserProfileModel()
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                userDataStateFlow.value = user
            }
            .addOnFailureListener { exception ->
                userDataStateFlow.value =
                    FirebaseUserProfileModel(errorException = Exception(LanguageKey.createUserErrorMessage))
                Timber.e("Error creating user: ${exception.message}")
            }
    }

    private suspend fun isUsernameInUse(username: String): Boolean {
        val result = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .whereEqualTo("username", username)
            .get()
            .await()
        return !result.isEmpty
    }

    override fun deleteUser(uid: String) {
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .document(uid)
            .delete()
            .addOnSuccessListener {
                userDataStateFlow.value = FirebaseUserProfileModel()
            }
            .addOnFailureListener {
                userDataStateFlow.value =
                    FirebaseUserProfileModel(errorException = Exception(LanguageKey.deleteUserErrorMessage))
            }
    }

    override fun getUserProfile(firebaseProfileUserModel: FirebaseUserProfileModel) {
        val uid = firebaseProfileUserModel.userId
        fetchAndEmitUserProfile(
            uid = uid,
            base = firebaseProfileUserModel,
            afterAction = null
        )
    }

    override fun getUserProfileForAutoLogin(
        firebaseProfileUserModel: FirebaseUserProfileModel,
        afterAction: ((Boolean) -> Unit),
    ) {
        val uid = firebaseProfileUserModel.userId
        fetchAndEmitUserProfile(
            uid = uid,
            base = firebaseProfileUserModel,
            afterAction = afterAction
        )
    }

    private fun fetchAndEmitUserProfile(
        uid: String,
        base: FirebaseUserProfileModel,
        afterAction: ((Boolean) -> Unit)?,
    ) {
        firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener { userDoc ->
                firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
                    .document(uid)
                    .collection("userProperty")
                    .document("biography")
                    .get()
                    .addOnSuccessListener { propDoc ->
                        val property = propDoc.toObject(FirebaseUserPropertyModel::class.java)
                        val loaded = userDoc.toObject(FirebaseUserProfileModel::class.java)

                        val merged = loaded?.copy(
                            biography = property?.biography.orEmpty(),
                            lastSignInTimestamp = base.lastSignInTimestamp,
                            isAnonymous = base.isAnonymous
                        )

                        if (merged != null && merged.userId == uid) {
                            userDataStateFlow.value = merged
                            afterAction?.invoke(true)
                        } else {
                            createProfile(base)
                            afterAction?.invoke(true)
                        }
                    }
                    .addOnFailureListener { e ->
                        userDataStateFlow.value = FirebaseUserProfileModel(errorException = e)
                        afterAction?.invoke(false)
                    }
            }
            .addOnFailureListener { e ->
                userDataStateFlow.value = FirebaseUserProfileModel(errorException = e)
                afterAction?.invoke(false)
            }
    }

    override fun getUsername(): String {
        return userProfileDataStateFlow.value.username
    }

    override fun getUserId(): String {
        return userProfileDataStateFlow.value.userId
    }

    override fun getUserProfileModel(): MutableStateFlow<FirebaseUserProfileModel> {
        return userDataStateFlow
    }

    override fun isMyContent(contentUsername: String): Boolean {
        return userDataStateFlow.value.username == contentUsername
    }

    override fun updateUserNotificationToken(notificationToken: String) {
        val userId = getUserId()
        val userDocRef =
            firestore.collection(FirebaseDatabaseKeys.userList)
                .document(FirebaseDatabaseKeys.generalUserList)
                .collection("users")
                .document(userId)

        userDocRef.update("notificationToken", notificationToken)
            .addOnSuccessListener { Timber.d("Notification token updated successfully") }
            .addOnFailureListener { exception -> Timber.e("Error updating notification token: ${exception.message}") }
    }

    override fun getUserProfileWithUserId(userId: String): Flow<FirebaseUserProfileModel> {
        return flow {
            try {
                val userDoc = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
                    .document(userId)
                    .get()
                    .await()

                val userPropertyDoc = firestore.collection(FirebaseUserDatabaseKey.USER_COLLECTION)
                    .document(userId)
                    .collection("userProperty")
                    .document("biography")
                    .get()
                    .await()

                val profile = userDoc.toObject(FirebaseUserProfileModel::class.java)
                val property = userPropertyDoc.toObject(FirebaseUserPropertyModel::class.java)

                if (profile != null) {
                    val merged = profile.copy(biography = property?.biography.orEmpty())
                    userDataStateFlow.value = merged
                    emit(merged)
                } else {
                    throw GeneralException("User profile not found for userId: $userId")
                }
            } catch (e: Exception) {
                userDataStateFlow.value = FirebaseUserProfileModel(errorException = e)
                throw e
            }
        }
    }

    override fun updateUserProfileProperty(updatedUser: UserProfileProperty) {
        userProfileDataStateFlow.value = updatedUser
    }
}
