package com.oyetech.domain.repository.firebase

interface FirebaseUserPropertyRepository {
    suspend fun updateBiography(userId: String, biography: String): Result<Unit>

    val updateOperationSharedEvent: kotlinx.coroutines.flow.MutableSharedFlow<Unit>
}
