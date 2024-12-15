package com.oyetech.domain.repository

import com.oyetech.models.entity.chat.ChatPreferencesDataResponse
import com.oyetech.models.entity.user.UserConnectResponseData
import com.oyetech.models.entity.user.UserDetailDataResponse
import com.oyetech.models.entity.user.UserPersonalInfoDataResponse
import com.oyetech.models.entity.user.UserPreferencesDataResponse
import com.oyetech.models.entity.user.UserProfileImageResponseData
import com.oyetech.models.entity.user.UserPropertyDataResponse
import com.oyetech.models.entity.userNotification.UserNotificationResponseData
import com.oyetech.models.postBody.biography.SetBiographyRequestBody
import com.oyetech.models.postBody.location.LocationRequestBody
import com.oyetech.models.postBody.messages.ReportUserRequestBody
import com.oyetech.models.postBody.user.OrderUserImageRequestBody
import com.oyetech.models.postBody.user.RemoveProfileImageRequestBody
import com.oyetech.models.postBody.user.UserOperationRequestBody
import com.oyetech.models.postBody.world.UserLanguageRequestBody
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
Created by Erdi Özbek
-19.03.2022-
-15:59-
 **/

interface UserOperationRepository {
    suspend fun getUserProfileProperty(userOperationRequestBody: UserOperationRequestBody): Flow<UserDetailDataResponse>

    suspend fun setUserLocation(params: LocationRequestBody): Flow<Boolean>

    suspend fun setUserBiography(params: SetBiographyRequestBody): Flow<Boolean>

    suspend fun getUserPreferences(): Flow<UserPropertyDataResponse>

    suspend fun setUserPreferences(
        params: UserPreferencesDataResponse,
    ): Flow<UserPropertyDataResponse>

    suspend fun getChatPreferences(): Flow<ChatPreferencesDataResponse>

    suspend fun setChatPreferences(
        params: ChatPreferencesDataResponse,
    ): Flow<Boolean>

    suspend fun followUser(params: UserOperationRequestBody): Flow<Boolean>

    suspend fun unFollowUser(params: UserOperationRequestBody): Flow<Boolean>

    suspend fun blockUser(params: UserOperationRequestBody): Flow<Boolean>

    suspend fun unblockUser(params: UserOperationRequestBody): Flow<Boolean>

    suspend fun likeUser(params: UserOperationRequestBody): Flow<Boolean>

    suspend fun unlikeUser(params: UserOperationRequestBody): Flow<Boolean>
    suspend fun getUserProfileImages(params: UserOperationRequestBody): Flow<List<UserProfileImageResponseData>>
    suspend fun removeUserProfileImage(params: RemoveProfileImageRequestBody): Flow<Boolean>
    suspend fun orderProfileImages(params: OrderUserImageRequestBody): Flow<Boolean>
    suspend fun uploadUserProfileImage(profileImage: File): Flow<List<UserProfileImageResponseData>>

    suspend fun getUserFollowers(params: UserOperationRequestBody): Flow<List<UserConnectResponseData>>
    suspend fun getUserFollowings(params: UserOperationRequestBody): Flow<List<UserConnectResponseData>>
    suspend fun getUserProfileLikes(params: UserOperationRequestBody): Flow<List<UserConnectResponseData>>
    suspend fun getLastNotifications(): Flow<List<UserNotificationResponseData>>

    suspend fun reportUser(params: ReportUserRequestBody): Flow<Boolean>
    suspend fun getBlockedUsers(): Flow<List<UserConnectResponseData>>
    suspend fun getUserPersonalInfo(): Flow<UserPersonalInfoDataResponse>
    suspend fun setUserPersonalInfo(params: UserPersonalInfoDataResponse): Flow<Boolean>
    suspend fun setUserLanguage(params: UserLanguageRequestBody): Flow<Boolean>
}
