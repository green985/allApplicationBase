package com.oyetech.localnotifications.impl

import android.app.PendingIntent
import com.oyetech.corecommon.contextHelper.getIntentFlagUpdateWithInMutable
import com.oyetech.corecommon.contextHelper.getMainActivityStartIntent
import com.oyetech.models.utils.const.HelperConstant

/**
Created by Erdi Özbek
-11.04.2023-
-18:54-
 **/

fun LocalNotificationRepositoryImplNew.generateMainPendingIntent(): PendingIntent {
    var intent = context.getMainActivityStartIntent()

    val alarmIntent =
        PendingIntent.getService(
            context,
            HelperConstant.LOCAL_NOTIFICATION_ALARM_ID,
            intent,
            getIntentFlagUpdateWithInMutable()
        )

    return alarmIntent
}


