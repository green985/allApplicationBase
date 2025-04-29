package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.userList.FirebaseUserListModel
import kotlinx.coroutines.flow.Flow

interface FirebaseUserListOperationRepository {
    fun getRandomUsersFromDatabase(): Flow<List<FirebaseUserListModel>>
    fun setVisibleByUserId(userId: String)
    suspend fun removeUserFromUserList(): Flow<Unit>
    suspend fun addUserToUserList(): Flow<Unit>
}
