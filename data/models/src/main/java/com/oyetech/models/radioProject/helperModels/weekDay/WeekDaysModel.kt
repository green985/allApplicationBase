package com.oyetech.models.radioProject.helperModels.weekDay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
Created by Erdi Özbek
-10.12.2022-
-16:15-
 **/

@Parcelize
data class WeekDaysModel(
    var name: String,
    var isSelected: Boolean = false,
    var id: Int,
) : Parcelable

fun generateWeekDaysList(): ArrayList<WeekDaysModel> {
    val weekOfCalendar = arrayOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY
    )
    var list = arrayListOf<WeekDaysModel>()
    weekOfCalendar.forEach {
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, it)
        var dayNameString = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)

        list.add(WeekDaysModel(dayNameString, isSelected = true, id = it))

    }

    return list

}