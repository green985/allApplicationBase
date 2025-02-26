package com.oyetech.domain.repository.firebase

import kotlinx.coroutines.flow.Flow

interface FirebaseUserListOperationRepository {
    suspend fun addUserToUserList(): Flow<Unit>
}
