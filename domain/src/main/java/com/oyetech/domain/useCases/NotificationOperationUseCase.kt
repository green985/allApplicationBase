package com.oyetech.domain.useCases

import com.oyetech.domain.repository.NotificationRepository
import com.oyetech.models.postBody.notification.NotificationTokenRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-28.03.2022-
-17:54-
 **/

class NotificationOperationUseCase(private var repository: NotificationRepository) {

    suspend fun sendDeviceToken(params: NotificationTokenRequestBody): Flow<Boolean> {
        return repository.sendDeviceToken(params)
    }
}
