package com.oyetech.notificationmodule

import com.oyetech.models.notificationModels.FormNotificationData
import com.oyetech.notificationmodule.NotificationConst.NOTIFICATION_DATA_FORM_ID
import com.oyetech.notificationmodule.NotificationConst.NOTIFICATION_DATA_USER_ID
import com.oyetech.notificationmodule.NotificationConst.NOTIFICATION_TYPE_FORM_RESULT_READY
import com.oyetech.notificationmodule.NotificationConst.NOTIFICATION_TYPE_KEY
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber

/**
Created by Erdi Özbek
-26.03.2026-
 **/

class NotificationHandlerHelper {

    private val _formNotificationFlow =
        MutableSharedFlow<FormNotificationData>(extraBufferCapacity = 1)
    val formNotificationFlow: SharedFlow<FormNotificationData> =
        _formNotificationFlow.asSharedFlow()

    fun handle(data: Map<String, String>) {
        when (data[NOTIFICATION_TYPE_KEY]) {
            NOTIFICATION_TYPE_FORM_RESULT_READY -> handleFormResultReady(data)
            else -> Timber.d("NotificationHandlerHelper: unknown type -> ${data[NOTIFICATION_TYPE_KEY]}")
        }
    }

    private fun handleFormResultReady(data: Map<String, String>) {
        val formId = data[NOTIFICATION_DATA_FORM_ID]
        val userId = data[NOTIFICATION_DATA_USER_ID]

        if (formId.isNullOrBlank() || userId.isNullOrBlank()) {
            Timber.d("NotificationHandlerHelper: formId or userId is missing in form_result_ready notification")
            return
        }

        Timber.d("NotificationHandlerHelper: form_result_ready -> formId=$formId, userId=$userId")
        _formNotificationFlow.tryEmit(FormNotificationData(formId = formId, userId = userId))
    }
}
