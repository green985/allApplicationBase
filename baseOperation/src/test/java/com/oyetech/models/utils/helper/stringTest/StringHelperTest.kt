package com.oyetech.models.utils.helper.stringTest

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
Created by Erdi Özbek
-23.08.2022-
-15:52-
 **/

@RunWith(JUnit4::class)
class StringHelperTest {
    var formatString = "%d new messages from %d people"

    var formatString2 = "%2${'$'}d new messages from %1${'$'}d people"

    var resultString = "12 new messages from 55 people"

    @Test
    fun formatedString() {
        var formatedString = formatString.format(12, 55)

        assert(formatedString == resultString)
    }

    @Test
    fun formatedStringDifferentLocation() {
        var formatedString = formatString2.format(12, 55)

        assert(formatedString == resultString)
    }
}
