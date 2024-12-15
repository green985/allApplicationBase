package com.oyetech.domain.useCases

import com.oyetech.domain.repository.UserOperationRepository
import com.oyetech.models.entity.user.UserConnectResponseData
import com.oyetech.models.postBody.messages.ReportUserRequestBody
import com.oyetech.models.postBody.user.UserOperationRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
Created by Erdi Özbek
-12.06.2022-
-16:23-
 **/

class UserConnectOperationUseCase(private val repository: UserOperationRepository) {

    suspend fun followUser(params: UserOperationRequestBody): Flow<Boolean> {
        return repository.followUser(params)
    }

    suspend fun unFollowUser(params: UserOperationRequestBody): Flow<Boolean> {
        return repository.unFollowUser(params)
    }

    suspend fun blockUser(params: UserOperationRequestBody): Flow<Boolean> {
        return repository.blockUser(params)
    }

    suspend fun unblockUser(params: UserOperationRequestBody): Flow<Boolean> {
        return repository.unblockUser(params)
    }

    suspend fun likeUser(params: UserOperationRequestBody): Flow<Boolean> {
        return repository.likeUser(params)
    }

    suspend fun unlikeUser(params: UserOperationRequestBody): Flow<Boolean> {
        return repository.unlikeUser(params)
    }

    suspend fun reportUser(params: ReportUserRequestBody): Flow<Boolean> {
        return repository.reportUser(params)
    }

    suspend fun getUserFollowers(params: UserOperationRequestBody): Flow<List<UserConnectResponseData>> {
        return repository.getUserFollowers(params)
    }

    suspend fun getBlockedUsers(): Flow<List<UserConnectResponseData>> {
        return repository.getBlockedUsers().map {
            it.also {
                it.map {
                    it.isFollowedByMe = true
                }
            }
        }
    }

    suspend fun getUserFollowings(params: UserOperationRequestBody): Flow<List<UserConnectResponseData>> {
        return repository.getUserFollowings(params)
    }

    suspend fun getUserProfileLikes(params: UserOperationRequestBody): Flow<List<UserConnectResponseData>> {
        return repository.getUserProfileLikes(params)
    }
}
