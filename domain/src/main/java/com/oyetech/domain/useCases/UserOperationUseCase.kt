package com.oyetech.domain.useCases

import com.oyetech.domain.repository.UserOperationRepository
import com.oyetech.models.entity.chat.ChatPreferencesDataResponse
import com.oyetech.models.entity.user.UserDetailDataResponse
import com.oyetech.models.entity.user.UserPersonalInfoDataResponse
import com.oyetech.models.entity.user.UserProfileImageResponseData
import com.oyetech.models.entity.userNotification.UserNotificationResponseData
import com.oyetech.models.postBody.biography.SetBiographyRequestBody
import com.oyetech.models.postBody.location.LocationRequestBody
import com.oyetech.models.postBody.messages.ReportUserRequestBody
import com.oyetech.models.postBody.user.OrderUserImageRequestBody
import com.oyetech.models.postBody.user.RemoveProfileImageRequestBody
import com.oyetech.models.postBody.user.UserOperationRequestBody
import com.oyetech.models.postBody.world.UserLanguageRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.io.File

/**
Created by Erdi Özbek
-22.05.2022-
-17:18-
 **/

class UserOperationUseCase(private val repository: UserOperationRepository) {

    suspend fun getUserProfileProperty(userOperationRequestBody: UserOperationRequestBody): Flow<UserDetailDataResponse> {
        return repository.getUserProfileProperty(userOperationRequestBody)
    }

    suspend fun setUserLocation(locationRequestBody: LocationRequestBody): Flow<Boolean> {
        return repository.setUserLocation(locationRequestBody)
    }

    suspend fun getUserPersonalInfo(): Flow<UserPersonalInfoDataResponse> {
        return repository.getUserPersonalInfo()
    }

    suspend fun setUserPersonalInfo(params: UserPersonalInfoDataResponse): Flow<Boolean> {
        return repository.setUserPersonalInfo(params)
    }

    suspend fun setUserLanguage(params: UserLanguageRequestBody): Flow<Boolean> {
        return repository.setUserLanguage(params)
    }

    suspend fun setChatPreferences(chatPreferencesDataResponse: ChatPreferencesDataResponse): Flow<Boolean> {
        return repository.setChatPreferences(chatPreferencesDataResponse)
    }

    suspend fun getUserProfileImages(userOperationRequestBody: UserOperationRequestBody): Flow<List<UserProfileImageResponseData>> {
        return repository.getUserProfileImages(userOperationRequestBody)
    }

    suspend fun removeUserProfileImage(removeProfileImageRequestBody: RemoveProfileImageRequestBody): Flow<Boolean> {
        return repository.removeUserProfileImage(removeProfileImageRequestBody)
    }

    suspend fun orderProfileImages(params: OrderUserImageRequestBody): Flow<Boolean> {
        return repository.orderProfileImages(params)
    }

    suspend fun setUserBiography(params: SetBiographyRequestBody): Flow<Boolean> {
        return repository.setUserBiography(params)
    }

    suspend fun getLastNotifications(): Flow<List<UserNotificationResponseData>> {
        return repository.getLastNotifications()
    }

    suspend fun reportUser(body: ReportUserRequestBody): Flow<Boolean> {
        return repository.reportUser(body)
    }

    suspend fun getChatPreferences(): Flow<ChatPreferencesDataResponse> {
        return repository.getChatPreferences()
    }

    fun uploadUserProfileImage(
        imageFile: File,
    ): Flow<List<UserProfileImageResponseData>> {
        return flow {
            emitAll(repository.uploadUserProfileImage(imageFile))
        }
    }
}
