package com.oyetech.localnotifications.impl

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import com.oyetech.corecommon.contextHelper.getIntentFlagUpdateWithInMutable
import com.oyetech.cripto.activityArgs.AlarmBundleKey
import com.oyetech.domain.repository.LocalNotificationRepository
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.localnotifications.alarmm.LocalNotificationAlarmReceiver
import com.oyetech.localnotifications.consts.LocalNotificationAlarmConst
import com.oyetech.models.utils.helper.TimeFunctions
import timber.log.Timber
import java.util.Calendar

/**
Created by Erdi Özbek
-11.04.2023-
-18:38-
 **/

class LocalNotificationRepositoryImplNew(
    var context: Context,
    var sharedOperationRepository: SharedHelperRepository,
) : LocalNotificationRepository {

    private var alarmPrayID = LocalNotificationAlarmConst.ALARM_PRAY_ID
    private var alarmVerseID = LocalNotificationAlarmConst.ALARM_VERSE_ID

    override fun setLocalNotificationsAlarm() {
        sharedOperationRepository.setFirstAlarmSetTimeMilis()
        // removeLocalNotificationAlarm()

        setAlarmForPrays()
        setAlarmForVerses()
    }

    override fun isCanSetAlarm(): Boolean {
        var lastAlarmSetMilis = sharedOperationRepository.getFirstAlarmSetTimeMilis()
        if (lastAlarmSetMilis == 0L) {
            return true
        }

        var isTimeExpired = TimeFunctions.isTimeExpiredFromParamHourWithMilis(
            lastAlarmSetMilis,
            TimeFunctions.A_WEEK_TIME_HOUR
        )

        Timber.d("isTineeenmememe == " + isTimeExpired)

        return isTimeExpired
    }

    private fun controlIsSameDayScheduleAlarm(): Boolean {
        // control alarm operation already set in same day.
        var firstAlarmSetTimeMilis = sharedOperationRepository.getFirstAlarmSetTimeMilis()

        if (firstAlarmSetTimeMilis == 0L) {
            return false
        }

        var isSameDay =
            !TimeFunctions.isTimeExpiredFromParamHourWithMilis(firstAlarmSetTimeMilis, 24)

        return isSameDay
    }

    private fun setAlarmWithTime(alarmId: Int, dataList: List<Any>) {
        var timePair = controlTimeForUnwantedAlarm(alarmId)

        //  Timber.d("time pairrr =" + timePair)
        // Timber.d("time pairrr count =" + count)

        dataList.forEachIndexed { index, alarmData ->
            Timber.d("alarm data == " + alarmData.toString())
            startAlarm(
                timePair.first,
                timePair.second,
                alarmId = alarmId,
                alarmCounter = index,
                data = alarmData
            )
        }

        repeat(dataList.size) {
            // startAlarm(timePair.first, timePair.second, alarmId = alarmId, alarmCounter = it)
        }
    }

    private fun controlTimeForUnwantedAlarm(alarmId: Int): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        if (alarmId == LocalNotificationAlarmConst.ALARM_PRAY_ID) {
            calendar.timeInMillis = System.currentTimeMillis()
        } else {
            calendar.timeInMillis = System.currentTimeMillis() + TimeFunctions.calculateHourMilis(2)
        }

        var hour = calendar[Calendar.HOUR_OF_DAY]
        var min = calendar[Calendar.MINUTE]

        if (hour > 0 && hour < 8) {
            hour = 23
            min = 0
        }

        Timber.d("calculated new hour  ?=?? " + hour)

        return Pair(hour, min)
    }

    @SuppressLint("MissingPermission")
    private fun startAlarm(
        selectedHour: Int,
        selectedMin: Int,
        alarmCounter: Int,
        alarmId: Int,
        data: Any,
    ) {
        // will be fixed.
        var alarmIntent = getPendingIntentt(alarmId + alarmCounter, data = data)

        val alarmMgr = getAlarmManagerr()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = selectedHour
        calendar[Calendar.MINUTE] = selectedMin
        calendar[Calendar.SECOND] = 0

        var afterDay = calculateAfterDayForAlarm(alarmCounter)

        Timber.d("after day  == " + afterDay)

        calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] + afterDay

        Timber.d("date = = = =" + TimeFunctions.getDateFromLongWithHour(calendar.timeInMillis))

        alarmMgr?.setAlarmClock(AlarmClockInfo(calendar.timeInMillis, alarmIntent), alarmIntent)
    }

    private fun calculateAfterDayForAlarm(alarmCounter: Int): Int {
        var alarmCounterr = alarmCounter + 1
        return alarmCounterr
    }

    // TODO
    private fun removeLocalNotificationAlarm() {
        /*
                val alarmManager = getAlarmManagerr()

                var alarmId = LocalNotificationAlarmConst.ALARM_ID_DAY_2
                repeat(LocalNotificationAlarmConst.totalAlarmCount) {
                    val pendingIntent = getPendingIntentt(alarmId)
                    if (pendingIntent != null && alarmManager != null) {
                        alarmManager.cancel(pendingIntent)
                    }
                    alarmId += 1
                }

         */
    }

    fun getAlarmManagerr(): AlarmManager? {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        return alarmManager
    }

    fun getPendingIntentt(alarmId: Int, data: Any): PendingIntent? {
        val intent = Intent(context, LocalNotificationAlarmReceiver::class.java)
        intent.putExtra(AlarmBundleKey.LOCAL_NOTIFICATION_ALARM_ID, alarmId)
        if (data is String) {
            intent.putExtra(AlarmBundleKey.LOCAL_NOTIFICATION_PRAY_DATA, data)
        } else {
            if (data is Parcelable) {
                intent.putExtra(AlarmBundleKey.LOCAL_NOTIFICATION_VERSE_DATA, data)
            }
        }

        val alarmIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                getIntentFlagUpdateWithInMutable()
            )

        return alarmIntent
    }

    fun setAlarmForVerses() {

        var verseList = sharedOperationRepository.getLocalNotificationVerseList()
        if (verseList.isNullOrEmpty()) {
            return
        }

        var count = verseList.size
        setAlarmWithTime(alarmVerseID, verseList)
    }

    fun setAlarmForPrays() {
        var prayList = sharedOperationRepository.getLocalNotificationPrayList()
        if (prayList.isNullOrEmpty()) {
            return
        }

        setAlarmWithTime(alarmPrayID, prayList)
    }


}