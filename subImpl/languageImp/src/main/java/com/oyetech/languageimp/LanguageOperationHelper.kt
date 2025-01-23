package com.oyetech.languageimp

import com.oyetech.cripto.stringKeys.SharedPrefKey
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.firebase.FirebaseLanguageOperationRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper
import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper.Companion.languageErrorSingleLiveEvent
import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper.Companion.languageHashMap
import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper.Companion.startWithVM
import com.oyetech.models.errors.ErrorMessage
import com.oyetech.models.firebaseModels.language.FirebaseLanguageResponseData
import com.oyetech.models.firebaseModels.language.FirebaseLanguageResponseDataWrapper
import com.oyetech.models.firebaseModels.language.toResponseData
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.helper.TimeFunctions
import com.oyetech.tools.coroutineHelper.retryWhenWithExpDelay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-18.01.2025-
-17:32-
 **/

class LanguageOperationHelper(
    private val sharedOperationUseCase: SharedOperationRepository,
    private val firebaseLanguageOperationRepository: FirebaseLanguageOperationRepository,
) {

    var isInit = false

    init {
        Timber.d("langugage helper inittttt")
    }

    private fun initSomeText() {
        ErrorMessage.unknownErrorMessage = LanguageKey.internetConnectionErrorText
    }

    fun initLanguageHelper(startWithVM: Boolean) {
        LocalLanguageHelper.startWithVM = startWithVM

        var textResourcesDataResponse = sharedOperationUseCase.getFirebaseLanguageValue()
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
                firebaseLanguageOperationRepository.getApplicationTextResources(
                    languageCodeRequestBody
                ).single()
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
        val textResourcesDataResponse = sharedOperationUseCase.getFirebaseLanguageValue()
        if (textResourcesDataResponse == null) {
            // critical, language not not found and cant be download...
            languageErrorSingleLiveEvent.tryEmit(true)
            return
        }

        // use old one
        Timber.d("use old one ")
        generateLanguageHashMap(textResourcesDataResponse.toResponseData())
        languageErrorSingleLiveEvent.tryEmit(false)
    }

    private fun saveLanguageDataToDevice(it: List<FirebaseLanguageResponseData>) {
        val firebaseLanguageResponseDataWrapper = FirebaseLanguageResponseDataWrapper(it)
        sharedOperationUseCase.saveFirebaseLanguageData(firebaseLanguageResponseDataWrapper)
    }

    fun initLanguageData() {
        val languageCodeRequestBody = sharedOperationUseCase.getLanguageValueOrNull()
        if (languageCodeRequestBody == null) {
            // language model problem, get new one
            Timber.d("language model problem, get new one ")
            getLanguagePackage()
            return
        }

        val isExpired = controlLanguageModelExpired()

        if (isExpired && startWithVM) {
            // get new one
            Timber.d("is isExpired, get new one")
            getLanguagePackage()
            return
        }


        retrieveLanguageFromDevice()
    }

    private fun retrieveLanguageFromDevice() {
        val textResourcesDataResponse = sharedOperationUseCase.getFirebaseLanguageValue()
        if (textResourcesDataResponse == null) {
            Timber.d("textResourcesDataResponse null")
            getLanguagePackage()
            return
        }

        generateLanguageHashMap(textResourcesDataResponse.toResponseData())
        languageErrorSingleLiveEvent.tryEmit(false)
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

    private fun generateLanguageHashMap(translations: List<FirebaseLanguageResponseData>) {
        LocalLanguageHelper.languageHashMap.clear()
        translations.forEach {
            languageHashMap[it.key] = it.value
        }
    }


}