package com.oyetech.helper.language

import android.content.res.Resources
import android.os.Build
import androidx.core.os.ConfigurationCompat
import com.oyetech.core.coroutineHelper.retryWhenWithExpDelay
import com.oyetech.core.stringOperation.StringHelper.languageValueOf
import com.oyetech.core.utils.SingleLiveEvent
import com.oyetech.cripto.stringKeys.SharedPrefKey
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.domain.useCases.WorldOperationUseCase
import com.oyetech.extension.appIsOrWasInBackground
import com.oyetech.models.entity.language.TextResourcesDataResponse
import com.oyetech.models.entity.language.TranslationDataResponse
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

/**
Created by Erdi Özbek
-17.05.2022-
-01:33-
 **/

class LanguageHelper(
    private var sharedOperationUseCase: SharedOperationUseCase,
    private var worldOperationUseCase: WorldOperationUseCase,
) {

    var isInit = false

    init {
        Timber.d("langugage helper inittttt")
    }

    fun initLanguageHelper(startWithVM: Boolean) {
        LanguageHelper.startWithVM = startWithVM

        var textResourcesDataResponse = sharedOperationUseCase.getLanguageValueOrNull()
        if (textResourcesDataResponse == null) {
            Timber.d("textResourcesDataResponse null")
            isInit = false
        }


        if (isInit) {
            Timber.d("already init...")
            return
        }
        isInit = true
        initLanguageData()
        initSomeText()
    }

    private fun initSomeText() {
        ErrorMessage.unknownErrorMessage = LanguageKeySet.NO_INTERNET_CONNECTION
    }

    companion object {
        private var languageHashMap = HashMap<String, String>()

        var languageErrorSingleLiveEvent = SingleLiveEvent<Boolean>()

        var startWithVM = false

        internal fun getStringWithKey(languageKey: String): String {
            return languageHashMap[languageKey] ?: languageKey
        }

        fun getStringWithKeyNull(languageKey: String?): String? {
            return languageHashMap[languageKey]
        }

        fun getStringWithFormat(languageKey: String?, args: String?): String {
            var formattedString = languageHashMap[languageKey]
            if (formattedString.isNullOrBlank()) return ""

            try {
                return formattedString.languageValueOf(args) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return languageKey ?: ""
        }

        fun getStringWithFormatWithLanguageKey(formattedString: String?, args: String?): String {
            if (formattedString.isNullOrBlank()) return ""

            try {
                return formattedString.languageValueOf(args) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formattedString + "" + args ?: ""
        }

        fun getStringWithFormatWithLanguageKeyWithInt(
            formattedString: String?,
            args: Int?,
        ): String {
            if (formattedString.isNullOrBlank()) return ""

            try {
                return formattedString.languageValueOf(args) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return formattedString + "" + args ?: ""
        }

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

    private fun getLanguagePackage() {
        Timber.d("getLanguagePackage girdi")
        //
        var languageCode = sharedOperationUseCase.getBaseLanguageCode()
        Timber.d("language code = === " + languageCode)
        var languageCodeRequestBody = LanguageCodeRequestBody(languageCode = languageCode)

        GlobalScope.launch {
            try {
                getLanguageFlow(languageCodeRequestBody).single()
            } catch (e: Exception) {
                // problem...
                languagePackageDownloadProblemHandle()
            }
        }
    }

    fun getLanguageFlow(languageCodeRequestBody: LanguageCodeRequestBody): Flow<Unit> {
        return flow {
            emit(
                worldOperationUseCase.getApplicationTextResources(languageCodeRequestBody).single()
            )
        }.retryWhenWithExpDelay { cause, attempt, delay ->
            cause.printStackTrace()
            Timber.d("delayyyyy == " + delay)
            delay(delay)
            return@retryWhenWithExpDelay attempt < HelperConstant.TRY_COUNT
        }.catch {
            Timber.d("catch girdi")
            it.printStackTrace()
            languagePackageDownloadProblemHandle()
        }.map {
            Timber.d("text source collenct")
            saveLanguageDataToDevice(it)
            initLanguageData()
        }
    }

    private fun languagePackageDownloadProblemHandle() {
        var textResourcesDataResponse = sharedOperationUseCase.getLanguageValueOrNull()
        if (textResourcesDataResponse == null) {
            // critical, language not not found and cant be download...
            languageErrorSingleLiveEvent.postValue(true)
            return
        }

        // use old one
        Timber.d("use old one ")
        generateLanguageHashMap(textResourcesDataResponse.translations)
        languageErrorSingleLiveEvent.postValue(false)
    }

    private fun saveLanguageDataToDevice(it: TextResourcesDataResponse) {
        sharedOperationUseCase.saveLanguageData(it)
    }

    fun initLanguageData() {
        // will  be fixed.

        var isApplicationOn = !appIsOrWasInBackground()

        Timber.d("isApplication onnnnnn == " + isApplicationOn)

        var languageCodeRequestBody = sharedOperationUseCase.getLanguageValueOrNull()
        if (languageCodeRequestBody == null) {
            // language model problem, get new one
            Timber.d("language model problem, get new one ")
            getLanguagePackage()
            return
        }

        var isExpired = controlLanguageModelExpired()

        if (isExpired && startWithVM) {
            // get new one
            Timber.d("is isExpired, get new one")
            getLanguagePackage()
            return
        }


        retrieveLanguageFromDevice()
    }

    private fun retrieveLanguageFromDevice() {
        var textResourcesDataResponse = sharedOperationUseCase.getLanguageValueOrNull()
        if (textResourcesDataResponse == null) {
            Timber.d("textResourcesDataResponse null")
            getLanguagePackage()
            return
        }

        generateLanguageHashMap(textResourcesDataResponse.translations)
        languageErrorSingleLiveEvent.postValue(false)
    }

    private fun controlLanguageModelExpired(): Boolean {
        var isLanguageDataSaved =
            sharedOperationUseCase.controlIsKeyNotNull(SharedPrefKey.FirebaseTextResourcesDataResponse)
        var isLanguageTimeSaved =
            sharedOperationUseCase.controlIsKeyNotNull(SharedPrefKey.TextResourcesDataResponseTimeMilis)
        if (!isLanguageDataSaved && !isLanguageTimeSaved) {
            Timber.d("not saved model found, get new one")
            return true
        }

        var languageSavedTime = sharedOperationUseCase.getLanguageTimeValue()

        var isTimeExpired = TimeFunctions.isTimeExpiredFromParamHourWithMilis(
            languageSavedTime,
            HelperConstant.LANGUAGE_EXPIRED_TIME
        )

        if (isTimeExpired) {
            // get new language model...
            Timber.d("saved data expired, get new one ... ")
            return true
        }

        return false
    }

    private fun generateLanguageHashMap(translations: List<TranslationDataResponse>) {
        languageHashMap.clear()
        translations.forEach {
            languageHashMap[it.key] = it.translationValue
        }
    }
}