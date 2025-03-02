package com.oyetech.domain.repository.firebase

import com.oyetech.models.firebaseModels.userList.FirebaseUserListModel
import kotlinx.coroutines.flow.Flow

interface FirebaseUserListOperationRepository {
    suspend fun addUserToUserList(): Flow<Unit>
    suspend fun getRandomUsersFromDatabase(): Flow<List<FirebaseUserListModel>>
}
