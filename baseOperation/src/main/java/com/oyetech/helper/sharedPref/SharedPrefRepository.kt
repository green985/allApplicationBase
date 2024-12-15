package com.oyetech.helper.sharedPref

import com.google.gson.reflect.TypeToken
import com.oyetech.base.BaseViewModel
import com.oyetech.cripto.stringKeys.SharedPrefKey
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.models.entity.auth.AuthRequestResponse
import com.oyetech.models.entity.auth.TokenDataResponse
import com.oyetech.models.entity.language.TextResourcesDataResponse
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.alarm.DataAlarmModel
import com.oyetech.models.radioProject.helperModels.weekDay.WeekDaysModel
import com.oyetech.models.radioProject.helperModels.weekDay.generateWeekDaysList
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.helper.TimeFunctions
import timber.log.Timber
import java.util.Calendar

/**
Created by Erdi Özbek
-6.03.2022-
-00:03-
 **/

class SharedPrefRepository(
    private var sharedHelper: SharedHelper,
) : SharedOperationRepository {

    override fun getToken(): String {
        var userTokenResponse =
            sharedHelper.retrieveData(SharedPrefKey.USER_TOKEN_MODEL, TokenDataResponse::class.java)

        if (userTokenResponse == null) {
            return ""
        }
        return userTokenResponse.token
    }

    override fun isLogin(): Boolean {
        return getToken().isNotBlank()
    }

    override fun getRefreshToken(): String {
        var userAuthModel =
            getUserAuthModel()

        if (userAuthModel == null) {
            return ""
        }
        return userAuthModel.refreshToken.token
    }

    override fun getUserId(): Long {
        var userAuthModel = getUserAuthModel()

        if (userAuthModel == null) {
            return 0L
        }
        return userAuthModel.userId
    }

    override fun isUserLogin(): Boolean {
        if (getUserId() == 0L) {
            return false
        }

        if (getUserAuthModel() == null) {
            return false
        }
        return true
    }

    override fun getUserAuthModel(): AuthRequestResponse? {
        var userAuthModel =
            sharedHelper.retrieveData(
                SharedPrefKey.USER_AUTH_MODEL,
                AuthRequestResponse::class.java
            )
        return userAuthModel
    }

    override fun saveUserTokenData(tokenData: AuthRequestResponse?) {
        if (tokenData == null) return
        sharedHelper.addData(SharedPrefKey.USER_AUTH_MODEL, tokenData)
        sharedHelper.addData(SharedPrefKey.USER_TOKEN_MODEL, tokenData.accessToken)
        sharedHelper.addData(SharedPrefKey.USER_REFRESH_TOKEN_MODEL, tokenData.refreshToken)
    }

    override fun getUserNickname(): String {
        var userAuthModel = getUserAuthModel()

        if (userAuthModel == null) {
            return ""
        }
        return userAuthModel.nick
    }

    override fun getLastGuid(): String {
        var clientSecret = sharedHelper.getStringData(SharedPrefKey.Client_Secret, "") ?: ""
        return clientSecret
    }

    override fun getLanguageValueOrNull(): TextResourcesDataResponse? {
        var textResourcesDataResponse = sharedHelper.retrieveData(
            SharedPrefKey.TextResourcesDataResponse,
            TextResourcesDataResponse::class.java
        )
        return textResourcesDataResponse
    }

    override fun getLanguageTimeValue(): Long {
        return sharedHelper.getLongData(SharedPrefKey.TextResourcesDataResponseTimeMilis, 0L) ?: 0L
    }

    override fun getLastClientUniqId(): String {
        var clientSecret = sharedHelper.getStringData(SharedPrefKey.Client_Secret_Uniq, "") ?: ""
        return clientSecret
    }

    override fun removeSharedPrefValues() {
        BaseViewModel.deleteDatabaseLiveData.postValue(true)
        var languageData = getLanguageValueOrNull()
        var languageSaveTimeMilis = getLanguageTimeValue()
        sharedHelper.clear()
        if (languageData != null && languageSaveTimeMilis != 0L) {
            saveLanguageData(languageData, languageSaveTimeMilis)
        }
    }

    override fun controlIsKeyNotNull(key: String): Boolean {
        return sharedHelper.contains(key)
    }

    override fun saveLanguageData(it: TextResourcesDataResponse, withTimeMilis: Long?) {
        sharedHelper.addData(SharedPrefKey.TextResourcesDataResponse, it)
        if (withTimeMilis != null) {
            sharedHelper.putLongData(
                SharedPrefKey.TextResourcesDataResponseTimeMilis,
                withTimeMilis
            )
        } else {
            sharedHelper.putLongData(
                SharedPrefKey.TextResourcesDataResponseTimeMilis,
                TimeFunctions.getTimeMilis()
            )
        }
    }

    override fun getConversationListExpireFlag(): Boolean {
        var timeMilis = sharedHelper.getLongData(SharedPrefKey.CONVERSATION_LIST_TIME_FLAG, 0L)
        var isTimeExpired = TimeFunctions.isTimeExpiredFromParamHourWithMilis(
            timeMilis,
            HelperConstant.LANGUAGE_EXPIRED_TIME
        )

        if (isTimeExpired) {
            sharedHelper.putLongData(
                SharedPrefKey.CONVERSATION_LIST_TIME_FLAG,
                TimeFunctions.getTimeMilis()
            )
        }
        return isTimeExpired
    }

    override fun removeConversationListKey() {
        sharedHelper.removeData(SharedPrefKey.CONVERSATION_LIST_TIME_FLAG)
    }

    override fun autoResumeOnWiredHeadsetConnection(): Boolean {
        return true
        return sharedHelper.getBooleanData(SharedPrefKey.autoResumeOnWiredHeadsetConnection, false)
    }

    override fun autoResumeOnBluetoothA2dpConnection(): Boolean {
        return true

        return sharedHelper.getBooleanData(SharedPrefKey.autoResumeOnBluetoothA2dpConnection, false)
    }

    override fun pauseWhenNoisy(): Boolean {
        return sharedHelper.getBooleanData(SharedPrefKey.pauseWhenNoisy, false)
    }

    override fun saveLastRadioToDB(lastRadioModel: RadioStationResponseData) {
        sharedHelper.addData(SharedPrefKey.lastRadioModel, lastRadioModel)
    }

    override fun saveLastWeekDayList(lastList: ArrayList<WeekDaysModel>) {
        sharedHelper.addListData(SharedPrefKey.lastWeekDayModel, lastList)
    }

    override fun getLastSavedWeekDayList(): ArrayList<WeekDaysModel> {
        val turnsType = object : TypeToken<List<WeekDaysModel>>() {}
        var list = sharedHelper.retrieveListData(
            SharedPrefKey.lastWeekDayModel,
            turnsType
        )
        if (list.isNullOrEmpty()) {
            list = generateWeekDaysList()
            sharedHelper.addListData(SharedPrefKey.lastWeekDayModel, list)
        }

        var subList = arrayListOf<WeekDaysModel>()
        subList.addAll(list)
        return subList
    }

    override fun saveAlarm(dataAlarmModel: DataAlarmModel) {
        sharedHelper.addData(SharedPrefKey.lastAlarmModel, dataAlarmModel)
    }

    override fun getAlarm(): DataAlarmModel? {
        return sharedHelper.retrieveData(SharedPrefKey.lastAlarmModel, DataAlarmModel::class.java)
    }

    override fun getStartActionString(): String {
        return sharedHelper.getStringData(SharedPrefKey.Start_Action_String, "") ?: ""
    }

    override fun setStartPageActionString(selectedStartAction: String) {
        Timber.d("start action string === " + selectedStartAction)
        sharedHelper.putStringData(SharedPrefKey.Start_Action_String, selectedStartAction)
    }

    override fun getDomainList(): List<String> {
        Timber.d("get domain List === ")
        val typeToken = object : TypeToken<List<String>>() {}
        var domainList = sharedHelper.retrieveListData(SharedPrefKey.Domain_List, typeToken)

        if (domainList.isNullOrEmpty()) {
            return emptyList()
        }

        return domainList
    }

    override fun setDomainList(list: List<String>) {
        Timber.d("set domain List === " + list.toString())
        sharedHelper.addListData(SharedPrefKey.Domain_List, list)
    }

    override fun setFirstOpenApplicationTimeMilis(timeMilis: Long) {
        sharedHelper.putLongData(SharedPrefKey.Application_First_Open, timeMilis)
    }

    override fun getFirstOpenApplicationTimeMilis(): Long {
        var timeMilis = sharedHelper.getLongData(SharedPrefKey.Application_First_Open, 0L)
        return timeMilis
    }

    override fun setFirstAlarmSetTimeMilis() {
        var timeMilis = TimeFunctions.getTimeMilis()
        sharedHelper.putLongData(SharedPrefKey.Alarm_First_Set_Time_Milis, timeMilis)
    }

    override fun setLastUsageLocalNotificationTextIndex(lastNotificationIndex: Int) {
        sharedHelper.putIntData(SharedPrefKey.Last_Local_Notification_Text_Index, 0)
    }

    override fun getLastUsageLocalNotificationTextIndex(): Int {
        return sharedHelper.getIntData(SharedPrefKey.Last_Local_Notification_Text_Index, 0)
    }

    override fun getFirstAlarmSetTimeMilis(): Long {
        var timeMilis = sharedHelper.getLongData(SharedPrefKey.Alarm_First_Set_Time_Milis, 0L)
        return timeMilis
    }

    override fun getSavedLastRadio(): RadioStationResponseData? {
        var lastRadioStationResponseData = sharedHelper.retrieveData(
            SharedPrefKey.lastRadioModel,
            RadioStationResponseData::class.java
        )

        return lastRadioStationResponseData

    }

    fun setReviewAlreadyShown(status: Boolean) {
        sharedHelper.putBooleanData(SharedPrefKey.IS_REVIEW_ALREADY_SHOWN, status)
    }

    fun setReviewDontWantSee(status: Boolean) {

        var timeMilis = Calendar.getInstance().timeInMillis
        sharedHelper.putLongData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE_TIME, timeMilis)
        sharedHelper.putBooleanData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE, status)
    }

    fun getSharedHelperBase(): SharedHelper {
        return sharedHelper
    }
}
