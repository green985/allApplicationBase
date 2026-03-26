package com.oyetech.domain.repository

import com.oyetech.models.notificationModels.FormNotificationData
import kotlinx.coroutines.flow.SharedFlow

/**
Created by Erdi Özbek
-26.03.2026-
 **/

interface NotificationHandlerRepository {
    val formNotificationFlow: SharedFlow<FormNotificationData>
}

