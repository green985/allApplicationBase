package com.oyetech.models.utils.helper

import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.utils.const.HelperConstant
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Formatter
import java.util.GregorianCalendar
import java.util.Locale
import java.util.SimpleTimeZone
import java.util.TimeZone

/**
Created by Erdi Özbek
-6.03.2022-
-00:29-
 **/

@Suppress("ReturnCount")
object TimeFunctions {

    var A_WEEK_TIME_HOUR = (7 * 24).toLong()

    fun isTimeExpired(expiredDateString: String): Boolean {
        var expiredDate = getDateFromString(expiredDateString)
        if (expiredDate == null) {
            Timber.d("Expireddd problem with date parse")

            return true
        }
        var calendar = Calendar.getInstance()
        calendar.timeZone = SimpleTimeZone(0, "GMT")
        var currentDate = Date(calendar.timeInMillis)
        if (currentDate.after(expiredDate)) {
            Timber.d("Expireddd")
            return true
        } else {
            Timber.d("Expireddd false")
            return false
        }
    }

    fun getDateFromLongWithHour(timeMilis: Long): String {

        var outputDateFormat =
            getSimpleDateFormatTimeZone("dd.MM.yyyy HH:mm", isDeviceTimeZone = true)
        var outputDateString = ""

        try {
            val date = Date(timeMilis)
            outputDateString = outputDateFormat.format(date)
        } catch (e: Exception) {
            ErrorMessage.fetchException(e)
        }
        return outputDateString
    }

    fun isTimeExpiredFromParamHourWithMilis(expiredDateMilis: Long, afterHour: Long): Boolean {
        var expiredDate = Date(expiredDateMilis + calculateHourMilis(afterHour))

        var currentDate = Date(Calendar.getInstance().timeInMillis)
        if (currentDate.after(expiredDate)) {
            Timber.d("Expireddd")
            return true
        } else {
            Timber.d("Expireddd false")
            return false
        }
    }

    fun getDateFromString(sourceDate: String): Date? {
        if (sourceDate == "") return null
        var dateFormat = getSimpleDateFormatTimeZone(HelperConstant.DATE_FORMAT)
        try {
            val date = dateFormat.parse(sourceDate)
            return date
        } catch (e: Exception) {
            Timber.d("exception = " + e)
            e.printStackTrace()
            Timber.d("exception = " + e.message)
        }
        return null
    }

    fun getDateFromStringGTM0(sourceDate: String): Date? {
        if (sourceDate == "") return null
        var dateFormat = getSimpleDateFormatTimeZone(HelperConstant.DATE_FORMAT)
        try {
            val date = dateFormat.parse(sourceDate)
            return date
        } catch (e: Exception) {
            Timber.d("exception = " + e)
            e.printStackTrace()
            Timber.d("exception = " + e.message)
        }
        return null
    }

    fun getDateFromStringWithoutHour(dateString: String): String {
        var dateFormat1 = getSimpleDateFormatTimeZone(HelperConstant.DATE_FORMAT)
        var outputDateFormat = getSimpleDateFormatTimeZone("dd.MM.yyyy", isDeviceTimeZone = true)
        var outputDateString = ""

        try {
            val date = dateFormat1.parse(dateString)
            outputDateString = outputDateFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputDateString
    }

    fun getDateFromLongWithoutHour(timeMilis: Long): String {

        var outputDateFormat = getSimpleDateFormatTimeZone("dd.MM.yyyy", isDeviceTimeZone = true)
        var outputDateString = ""

        try {
            val date = Date(timeMilis)
            outputDateString = outputDateFormat.format(date)
        } catch (e: Exception) {
            ErrorMessage.fetchException(e)
        }
        return outputDateString
    }

    fun getDateFromLongWithoutHourLongMonthName(timeMilis: Long): String {

        var outputDateFormat = getSimpleDateFormatTimeZone("dd MMM yyyy", isDeviceTimeZone = true)
        var outputDateString = ""

        try {
            val date = Date(timeMilis)
            outputDateString = outputDateFormat.format(date)
        } catch (e: Exception) {
            ErrorMessage.fetchException(e)
        }
        return outputDateString
    }

    fun getDateFromLongJustHourSecond(timeMilis: Long): String {

        var outputDateFormat = getSimpleDateFormatTimeZone(
            HelperConstant.HOUR_SECOND_TIME_FORMAT,
            isDeviceTimeZone = true
        )
        var outputDateString = ""

        try {
            val date = Date(timeMilis)
            outputDateString = outputDateFormat.format(date)
        } catch (e: Exception) {
            ErrorMessage.fetchException(e)
        }
        return outputDateString
    }

    fun getDateStringFromLongMilis(timeMilis: Long): String {
        var outputDateFormat =
            getSimpleDateFormatTimeZone(HelperConstant.HOUR_TIME_FORMAT, isDeviceTimeZone = true)
        var outputDateString = ""

        try {
            val date = Date(timeMilis)
            outputDateString = outputDateFormat.format(date)
        } catch (e: Exception) {
            ErrorMessage.fetchException(e)
        }
        return outputDateString
    }

    fun getFullDateFromLongMilis(timeMilis: Long): String {
        var outputDateFormat =
            getSimpleDateFormatTimeZone(HelperConstant.DATE_FORMAT, isDeviceTimeZone = true)
        var outputDateString = ""

        try {
            val date = Date(timeMilis)
            outputDateString = outputDateFormat.format(date)
        } catch (e: Exception) {
            ErrorMessage.fetchException(e)
        }
        return outputDateString
    }

    fun getDateJustHour(mDate: String): String {

        var dateFormat1 = getSimpleDateFormatTimeZone(HelperConstant.DATE_FORMAT)
        var outputDateFormat =
            getSimpleDateFormatTimeZone(HelperConstant.HOUR_TIME_FORMAT, isDeviceTimeZone = true)
        var outputDateString = ""
        try {
            val date = dateFormat1.parse(mDate)
            outputDateString = outputDateFormat.format(date!!)
        } catch (e: Exception) {
            // e.printStackTrace()
        }
        return outputDateString
    }

    fun getDateBirthDayFormat(mDate: String): String {

        var dateFormat1 = getSimpleDateFormatTimeZone(HelperConstant.BIRTHDAY_DATE_FORMAT)
        var outputDateFormat =
            getSimpleDateFormatTimeZone(HelperConstant.HOUR_TIME_FORMAT, isDeviceTimeZone = true)
        var outputDateString = ""
        try {
            val date = dateFormat1.parse(mDate)
            outputDateString = outputDateFormat.format(date!!)
        } catch (e: Exception) {
            // e.printStackTrace()
        }
        return outputDateString
    }

    fun isSameDayLocalCalender(createdDateString: String): Boolean {
        var dateFormat1 = getSimpleDateFormatTimeZone("yyyy-MM-dd", isDeviceTimeZone = true)
        var createdDate = Date()
        try {
            createdDate = dateFormat1.parse(createdDateString)
            val cal1 = Calendar.getInstance()
            val cal2 = Calendar.getInstance()
            cal1.time = createdDate
            val sameDay =
                cal1[Calendar.DAY_OF_YEAR] === cal2[Calendar.DAY_OF_YEAR] &&
                        cal1[Calendar.YEAR] === cal2[Calendar.YEAR]

            return sameDay
        } catch (e: Exception) {
            return false
        }
    }

    fun calculateTimeForUnlockTime(unlockTime: String): String {
        var unlockTimeDate = getDateFromString(unlockTime)

        var unlockTimeMilis = unlockTimeDate?.time ?: return ""

        var currentTimeMilisUTC0 = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis

        var limitTimeMilis = unlockTimeMilis - currentTimeMilisUTC0

        var hourMinString = getDateFromLongJustHourSecond(limitTimeMilis)
        return hourMinString
    }

    fun calculateTimeForUnlockTimeIntArray(unlockTime: String): List<Int> {

        var dateFormat = getSimpleDateFormatTimeZone(HelperConstant.DATE_FORMAT)
        val date = try {
            dateFormat.parse(unlockTime)
        } catch (e: Exception) {
            Timber.d("exception = " + e)
            e.printStackTrace()
            Timber.d("exception = " + e.message)
            null
        }
        if (date == null) {
            return emptyList()
        }

        var unlockTimeDate = date

        var unlockTimeMilis = unlockTimeDate?.time ?: return listOf()

        var currentTimeMilisUTC0 = Calendar.getInstance().timeInMillis

        var limitTimeMilis = unlockTimeMilis - currentTimeMilisUTC0

        var arrayy = getTimeIntArray(limitTimeMilis)
        return arrayy
        // var hourMinString = getDateFromLongJustHourSecond(limitTimeMilis)

        var hourMinDate = Date(limitTimeMilis)

        var hourMinSecArray = buildList {
            this.add(hourMinDate.hours)
            this.add(hourMinDate.minutes)
            this.add(hourMinDate.seconds)
        }
        return hourMinSecArray
        Timber.d("milisssss == " + hourMinSecArray.toString())
    }

    private fun getTimeIntArray(limitTimeMilis: Long): List<Int> {
        val seconds = ((limitTimeMilis / 1000) % 60).toInt()
        val minutes = (limitTimeMilis / (1000 * 60) % 60).toInt()
        val hours = (limitTimeMilis / (1000 * 60 * 60) % 24).toInt()
        return buildList {
            this.add(hours)
            this.add(minutes)
            this.add(seconds)
        }
    }

    fun getSimpleDateFormatTimeZone(
        pattern: String,
        isDeviceTimeZone: Boolean = false,
    ): SimpleDateFormat {
        var sdf = SimpleDateFormat(pattern, Locale.ROOT)
        if (isDeviceTimeZone) {
            val mCalendar: Calendar = GregorianCalendar()
            val mTimeZone = mCalendar.timeZone
            sdf.timeZone = mTimeZone
        } else {
            sdf.timeZone = TimeZone.getTimeZone("UTC")
        }

        return sdf
    }

    fun calculateHourMilis(hour: Long): Long {
        return hour * 60 * 60 * 1000
    }

    fun calculateDayMilis(day: Long): Long {
        return day * 24 * 60 * 60 * 1000
    }

    private fun calculateMinMilis(min: Long): Long {
        return min * 60 * 1000
    }

    fun getTimeMilis(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun getBirthDayString(it: Long?, selection: Long): String {
        if (selection == 0L) {
            return ""
        }
        var date = Date(selection)
        var calendar = Calendar.getInstance()
        calendar.time = date

        var dayString = if (calendar.get(Calendar.DAY_OF_MONTH) > 9) {
            (calendar.get(Calendar.DAY_OF_MONTH)).toString()
        } else {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        }
        var monthString = if (calendar.get(Calendar.MONTH) + 1 > 9) {
            (calendar.get(Calendar.MONTH) + 1).toString()
        } else {
            "0" + (calendar.get(Calendar.MONTH) + 1)
        }
        var yearString = calendar.get(Calendar.YEAR)

        var birthDayString = buildString {
            this.append(dayString)
                .append(HelperConstant.DATE_FORMAT_SEPERATOR)
                .append(monthString)
                .append(HelperConstant.DATE_FORMAT_SEPERATOR)
                .append(yearString)
        }
        Timber.d("birthdayString == " + birthDayString)
        return birthDayString
    }

    fun prepareCalendarForBirthdaySection(): Calendar {

        val calendar = Calendar.getInstance()
        val calendarMin = Calendar.getInstance()
        calendarMin.set(Calendar.YEAR, 1920)

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 18)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return calendar
    }

    fun getHourMinSecFromLongString(duration: Long): String {
        val mFormatBuilder = StringBuilder()
        val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = duration / 1000
        //  videoDurationInSeconds = totalSeconds % 60;
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        mFormatBuilder.setLength(0)
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }


    }

    fun calculateToSecondToMinStringForm(seconds: Long): String {
        var min = seconds / 60
        var leftSecond = seconds - (min * 60)

        var timeString = "" + min + ":"
        var leftSecondString = if (leftSecond < 10) {
            "0" + leftSecond
        } else {
            leftSecond.toString()
        }
        return timeString.plus(leftSecondString)
    }

}
