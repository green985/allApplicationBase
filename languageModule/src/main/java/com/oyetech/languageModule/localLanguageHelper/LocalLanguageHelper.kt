package com.oyetech.languageModule.localLanguageHelper

import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.core.os.ConfigurationCompat
import com.oyetech.models.utils.moshi.deserializeHashMap
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.util.Locale
import kotlin.system.measureTimeMillis

/**
Created by Erdi Özbek
-24/04/2024-
-20:07-
 **/

class LocalLanguageHelper(private val context: Context) {

    init {
        Timber.d("LocalLanguageHelper init")
        val timeeee = measureTimeMillis {
            readAndInitLanguageHash("wallpaper_language_en.json")
        }
        Timber.d("LocalLanguageHelper init time: $timeeee")
    }

    companion object {
        var languageHashMap = HashMap<String, String>()
        var startWithVM = false

        var languageErrorSingleLiveEvent = MutableStateFlow<Boolean>(false)

        internal fun getStringWithKey(languageKey: String, defaultString: String = ""): String {
            if (languageHashMap.isEmpty()) {
                // if languageHashMap is empty, we need to init again.
                Timber.d("languageHashMap is empty, we need to init again.")
                return languageKey
//                return defaultString
            }

            val languageValue = languageHashMap[languageKey] ?: languageKey.plus("~~")
            return languageValue
        }

    }

    private fun readAndInitLanguageHash(fileName: String) {
        try {

            val jsonString = context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }

            val sss = jsonString.deserializeHashMap<String>()

            if (sss == null) {
                Timber.d("readJsonFile: sss is null")
                return
            }

            languageHashMap = HashMap(sss)

        } catch (e: Exception) {
            Timber.d("readJsonFile: ${e.message}")
            // we need to show just error, maybe we can do something more later.
            // languageErrorSingleLiveEvent.postValue(true)
        }
    }

    // will be used later, now we dont care about language code.
    fun getLocalLanguageCode(): String {
        var localCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val currentLocale =
                ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)
            currentLocale?.language
        } else {
            Locale.getDefault().language
        }
        if (localCode.isNullOrBlank()) {
            Timber.d("getLocalLanguageCode ==== " + null)
            localCode = "en"
        }
        return localCode
    }


}