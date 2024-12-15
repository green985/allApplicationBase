package com.oyetech.models.utils.helper.time

import com.oyetech.models.utils.helper.TimeFunctions.getDateFromString
import com.oyetech.models.utils.helper.TimeFunctions.getDateJustHour
import com.oyetech.models.utils.helper.TimeFunctions.isSameDayLocalCalender
import com.oyetech.models.utils.helper.TimeFunctions.isTimeExpired
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import timber.log.Timber

/**
 * Created by Erdi Özbek
 * -6.03.2022-
 * -14:12-
 */
@RunWith(JUnit4::class)
class TimeFunctionsTest {

    // var dateFormatString = "2022-03-07T11:09:39.2677291Z"
    var dateFormatString = "2022-03-26T12:29:29Z"

    @Test
    fun testIsTokenExpired() {
        var tokenExpiredFalse = isTimeExpired(dateFormatString)

        var dateFormatStringVar = "2022-03-01T11:09:39.2677291Z"
        var tokenExpiredTrue = isTimeExpired(dateFormatStringVar)
        assert(tokenExpiredTrue)

        assert(!tokenExpiredFalse)
    }

    @Test
    fun testExpireDate() {
        var formatedDate = getDateFromString(dateFormatString)
        assert(formatedDate != null)
    }

    @Test
    fun testTimeZone() {
        // var datee = "2022-07-19T10:48:15Z"
        var datee = "2022-03-26T12:29:29Z"
        var formatedDate = getDateJustHour(datee)
        Timber.d("formatedDate == " + formatedDate)
    }

    @Test
    fun testSameDay() {
        var isSameDay = isSameDayLocalCalender("2022-07-07T12:24:42Z")
        assert(isSameDay == true)
    }
}
