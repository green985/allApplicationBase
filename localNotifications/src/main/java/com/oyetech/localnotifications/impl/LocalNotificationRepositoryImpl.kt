package com.oyetech.localnotifications.impl

/**
Created by Erdi Özbek
-11.04.2023-
-18:38-
 **/
/*
class LocalNotificationRepositoryImpl(
    var context: Context,
    var sharedOperationRepository: SharedHelperRepository,
) : LocalNotificationRepository {

    private var alarmId = LocalNotificationAlarmConst.ALARM_ID_DAY_2

    private var alarmPrayID = LocalNotificationAlarmConst.ALARM_PRAY_ID
    private var alarmVerseID = LocalNotificationAlarmConst.ALARM_VERSE_ID

    fun setLocalNotificationsAlarm() {
        sharedOperationRepository.setFirstAlarmSetTimeMilis()
        removeLocalNotificationAlarm()

        setAlarmForPrays()
    }

    override fun setAlarmForPrays() {
    }

    fun setAlarmForLocalNotification() {
        var isSameDayControl = controlIsSameDayScheduleAlarm()

        /*
                if (isSameDayControl) {
                    Timber.d("already alarm active")
                    return
                }


         */
        // remove active alarms and set again...
        removeLocalNotificationAlarm()

        setAlarmWithTime()
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

    private fun setAlarmWithTime() {
        var timePair = controlTimeForUnwantedAlarm()

        Timber.d("time pairrr =" + timePair)

        repeat(LocalNotificationAlarmConst.totalAlarmCount) {
            startAlarm(timePair.first, timePair.second, alarmCounter = it)
        }
    }

    private fun controlTimeForUnwantedAlarm(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

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
    private fun startAlarm(selectedHour: Int, selectedMin: Int, alarmCounter: Int) {
        // will be fixed.
        var alarmIntent = getPendingIntentt(alarmId + alarmCounter)

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
        if (alarmCounterr < 5) {
            return 2 * alarmCounterr
        }

        if (alarmCounterr == 6) {
            return 14
        }

        if (alarmCounterr == 7) {
            return 20
        }

        if (alarmCounterr == 8) {
            return 30
        }


        return 2 * alarmCounterr
    }

    private fun removeLocalNotificationAlarm() {

        val alarmManager = getAlarmManagerr()

        var alarmId = LocalNotificationAlarmConst.ALARM_ID_DAY_2
        repeat(LocalNotificationAlarmConst.totalAlarmCount) {
            val pendingIntent = getPendingIntentt(alarmId)
            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }
            alarmId += 1
        }


    }

    fun getAlarmManagerr(): AlarmManager? {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        return alarmManager
    }

    fun getPendingIntentt(alarmId: Int): PendingIntent? {
        val intent = Intent(context, LocalNotificationAlarmReceiver::class.java)
        intent.putExtra(AlarmBundleKey.LOCAL_NOTIFICATION_ALARM_ID, alarmId)

        val alarmIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                getIntentFlagUpdateWithInMutable()
            )

        return alarmIntent
    }

    override fun setAlarmForVerses() {
        //TODO
    }

}

 */