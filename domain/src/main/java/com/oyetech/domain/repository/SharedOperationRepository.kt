package com.oyetech.domain.repository

import com.oyetech.models.entity.auth.AuthRequestResponse
import com.oyetech.models.entity.language.TextResourcesDataResponse
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.alarm.DataAlarmModel
import com.oyetech.models.radioProject.helperModels.weekDay.WeekDaysModel

/**
Created by Erdi Özbek
-21.11.2022-
-18:24-
 **/

interface SharedOperationRepository {
    fun getToken(): String
    fun isLogin(): Boolean
    fun getRefreshToken(): String
    fun getUserId(): Long
    fun isUserLogin(): Boolean
    fun getUserAuthModel(): AuthRequestResponse?
    fun saveUserTokenData(tokenData: AuthRequestResponse?)
    fun getUserNickname(): String
    fun getLastGuid(): String
    fun getLanguageValueOrNull(): TextResourcesDataResponse?
    fun getLanguageTimeValue(): Long
    fun getLastClientUniqId(): String
    fun removeSharedPrefValues()
    fun controlIsKeyNotNull(key: String): Boolean
    fun saveLanguageData(it: TextResourcesDataResponse, withTimeMilis: Long? = null)
    fun getConversationListExpireFlag(): Boolean
    fun removeConversationListKey()
    fun autoResumeOnWiredHeadsetConnection(): Boolean
    fun autoResumeOnBluetoothA2dpConnection(): Boolean
    fun pauseWhenNoisy(): Boolean

    fun getSavedLastRadio(): RadioStationResponseData?
    fun saveLastRadioToDB(lastRadioModel: RadioStationResponseData)

    fun saveLastWeekDayList(lastList: ArrayList<WeekDaysModel>)
    fun getLastSavedWeekDayList(): ArrayList<WeekDaysModel>

    fun saveAlarm(dataAlarmModel: DataAlarmModel)
    fun getAlarm(): DataAlarmModel?
    fun getStartActionString(): String
    fun setStartPageActionString(selectedStartAction: String)

    fun getDomainList(): List<String>
    fun setDomainList(list: List<String>)

    fun setFirstOpenApplicationTimeMilis(timeMilis: Long)
    fun getFirstOpenApplicationTimeMilis(): Long

    fun getFirstAlarmSetTimeMilis(): Long
    fun setFirstAlarmSetTimeMilis()

    fun setLastUsageLocalNotificationTextIndex(lastNotificationIndex: Int)
    fun getLastUsageLocalNotificationTextIndex(): Int


}