package com.oyetech.domain.repository

interface LocalNotificationRepository {

    fun setLocalNotificationsAlarm()
    fun isCanSetAlarm(): Boolean

}