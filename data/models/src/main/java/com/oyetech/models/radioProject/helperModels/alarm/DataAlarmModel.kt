package com.oyetech.models.radioProject.helperModels.alarm

import android.os.Parcelable
import androidx.annotation.Keep
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.weekDay.WeekDaysModel
import com.oyetech.models.radioProject.helperModels.weekDay.generateWeekDaysList
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

/**
Created by Erdi Özbek
-10.12.2022-
-18:18-
 **/

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class DataAlarmModel(

    // will be fixed.
    var alarmId: Int = Random(55555).nextInt(),

    var selectedRadioStation: RadioStationResponseData? = null,

    var selectedHour: Int = 0,
    var selectedMin: Int = 0,
    var selectedWeekDaysModel: ArrayList<WeekDaysModel> = generateWeekDaysList(),
    var isActive: Boolean = false,

    // will be fixed.
    var isRepeating: Boolean = true,

    ) : Parcelable