package com.oyetech.domain.repository

import com.oyetech.models.postBody.notification.NotificationTokenRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-28.03.2022-
-17:52-
 **/

interface NotificationRepository {

    suspend fun sendDeviceToken(params: NotificationTokenRequestBody): Flow<Boolean>
}
