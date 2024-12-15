package com.oyetech.helper

import com.oyetech.cripto.privateKeys.PrivateKeys

object BaseUrlConfigHelper {

    val BASE_URL = "https://api.nearbyanonymouschat.com/bible/"
    val BASE_URL_TEST = "https://api.nearbyanonymouschat.com/bible/"

    val API_SERVICE_BIBLE_BASE_URL = "https://api.scripture.api.bible/"

    var BASE_URL_RADIO_TEST = "https://all.api.radio-browser.info/json/"
    var BASE_DOMAIN = "at1.api.radio-browser.info"

    var BASE_DOMAIN_RADIO = "https://at1.api.radio-browser.info/json/"

    var flaggg = false

    fun getBaseUrl(): String {
        /*
        val isDeveloperModeActive =
            sharedHelper.getBooleanData(SharedPrefCons.DEVELOPER_MODE, false)
        Timber.d("isDeveloper Mode = " + isDeveloperModeActive)
        if (isDeveloperModeActive) {
            return BASE_URL_TEST
        } else {
            return BuildConfig.BASE_URL
        }

         */

        if (flaggg) {
            return BASE_URL_TEST
        } else {
            return BASE_URL
        }

        return BASE_URL_TEST
    }

    fun getBaseSocketUrl(): String {
        /*
        val isDeveloperModeActive =
            sharedHelper.getBooleanData(SharedPrefCons.DEVELOPER_MODE, false)
        Timber.d("isDeveloper Mode = " + isDeveloperModeActive)
        if (isDeveloperModeActive) {
            return BASE_SIGNALR_URL_TEST
        } else {
            return BuildConfig.BASE_SIGNALR_URL
        }

         */

        if (flaggg) {
            return PrivateKeys.SIGNAL_R_URL_TEST
        } else {
            return PrivateKeys.SIGNAL_R_URL
        }
    }
}
