package com.oyetech.notificationmodule

import com.oyetech.domain.repository.NotificationHandlerRepository
import com.oyetech.models.notificationModels.FormNotificationData
import kotlinx.coroutines.flow.SharedFlow

/**
Created by Erdi Özbek
-26.03.2026-
 **/

class NotificationHandlerRepositoryImpl(
    private val notificationHandlerHelper: NotificationHandlerHelper,
) : NotificationHandlerRepository {

    override val formNotificationFlow: SharedFlow<FormNotificationData>
        get() = notificationHandlerHelper.formNotificationFlow
}

