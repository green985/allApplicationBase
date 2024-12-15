package com.oyetech.domain.repository

import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.weekDay.WeekDaysModel
import com.oyetech.models.radioProject.helperModels.weekDay.generateWeekDaysList

/**
Created by Erdi Özbek
-13.12.2022-
-17:59-
 **/

interface AlarmOperationRepository {

    fun addAlarm(
        radioStationResponseData: RadioStationResponseData? = null,
        selectHour: Int,
        selectMin: Int,
        selectedWeekDaysModel: ArrayList<WeekDaysModel> = generateWeekDaysList(),
    )

    fun setEnableAlarm(alarmId: Int = 0, isEnable: Boolean)
}