package com.oyetech.radioservice.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.oyetech.domain.helper.isDebug
import com.oyetech.domain.repository.AlarmOperationRepository
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.alarm.DataAlarmModel
import com.oyetech.models.radioProject.helperModels.weekDay.WeekDaysModel
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.radioservice.serviceUtils.PlayerServiceUtils
import com.oyetech.radioservice.serviceUtils.ServiceConst
import timber.log.Timber
import java.util.Calendar

/**
Created by Erdi Özbek
-11.12.2022-
-00:22-
 **/

class AlarmManagerHelper(
    private var context: Context,
    private var sharedOperationUseCase: SharedOperationUseCase,
) : AlarmOperationRepository {

    // will be fixed later...
    override fun addAlarm(
        radioStationResponseData: RadioStationResponseData?,
        selectHour: Int,
        selectMin: Int,
        selectedWeekDaysModel: ArrayList<WeekDaysModel>,
    ) {
        var dataAlarmModel = DataAlarmModel(
            alarmId = getFreeAlarmId(),
            selectedRadioStation = radioStationResponseData,
            selectedHour = selectHour,
            selectedMin = selectMin,
            selectedWeekDaysModel = selectedWeekDaysModel,
            isActive = true
        )

        saveAlarm(dataAlarmModel)
    }

    private fun saveAlarm(dataAlarmModel: DataAlarmModel) {
        sharedOperationUseCase.saveAlarm(dataAlarmModel)
    }

    fun getFreeAlarmId(): Int {
        var lastAlarm = sharedOperationUseCase.getAlarmm()
        var idd = 1
        if (lastAlarm == null) {
            return idd
        } else {
            idd = lastAlarm.alarmId + 1
        }
        Timber.d("iddddd == " + idd)
        return idd
    }

    override fun setEnableAlarm(alarmId: Int, isEnable: Boolean) {
        var alarmModel = sharedOperationUseCase.getAlarmm()
        if (isEnable) {
            startAlarm(alarmModel)
        } else {
            stopAlarm(alarmModel)
            alarmModel?.isActive = false
            alarmModel?.let { saveAlarm(it) }
        }
    }

    fun getPendingIntentt(alarmId: Int): PendingIntent? {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(ServiceConst.ALARM_ID, alarmId)

        val alarmIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PlayerServiceUtils.getIntentFlagUpdateWithInMutable()
            )

        return alarmIntent
    }

    @SuppressLint("MissingPermission")
    private fun startAlarm(alarmModel: DataAlarmModel?) {

        if (alarmModel == null) {
            Timber.d("alarmModel null ")
            return
        }
        var alarmId = alarmModel.alarmId

        stopAlarm(alarmModel)

        val alarmIntent = getPendingIntentt(alarmId)

        val alarmMgr = getAlarmManagerr()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = alarmModel.selectedHour
        calendar[Calendar.MINUTE] = alarmModel.selectedMin
        calendar[Calendar.SECOND] = 0

        // if new calendar is in the past, move it 1 day ahead
        // add 1 min, to ignore already fired events

        // if new calendar is in the past, move it 1 day ahead
        // add 1 min, to ignore already fired events
        if (calendar.timeInMillis < System.currentTimeMillis() + 60) {
            if (context.isDebug()) {
                Log.d("ALARM", "moved ahead one day")
            }
            calendar.timeInMillis =
                calendar.timeInMillis + HelperConstant.ONE_DAY_IN_MILLIS
        }
        Timber.d("alarm startteee = " + alarmModel)
        alarmIntent?.let {
            alarmMgr?.setAlarmClock(AlarmClockInfo(calendar.timeInMillis, alarmIntent), alarmIntent)
        }

    }

    private fun stopAlarm(alarmModel: DataAlarmModel?) {
        if (alarmModel == null) {
            Timber.d("alarm model nulllll")
            return
        }
        val alarmManager = getAlarmManagerr()
        val pendingIntent = getPendingIntentt(alarmModel.alarmId)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun getAlarmManagerr(): AlarmManager? {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        return alarmManager
    }


}