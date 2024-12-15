package com.oyetech.domain.useCases

import com.oyetech.domain.repository.AlarmOperationRepository
import com.oyetech.models.radioProject.helperModels.alarm.DataAlarmModel

/**
Created by Erdi Özbek
-13.12.2022-
-18:01-
 **/

class AlarmOperationUseCase(private var repository: AlarmOperationRepository) {

    fun addAlarm(
        radioStationResponseData: DataAlarmModel?,
    ) {
        radioStationResponseData?.apply {
            repository.addAlarm(
                selectedRadioStation,
                selectedHour,
                selectedMin,
                selectedWeekDaysModel
            )
        }
    }

    fun setEnableAlarm(alarmId: Int = 0, isEnable: Boolean) {
        repository.setEnableAlarm(alarmId, isEnable)
    }

}