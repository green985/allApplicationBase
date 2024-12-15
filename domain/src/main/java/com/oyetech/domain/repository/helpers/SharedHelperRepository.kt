package com.oyetech.domain.repository.helpers

import com.oyetech.models.entity.NotificationSettingsData
import com.oyetech.models.entity.auth.AuthRequestResponse
import com.oyetech.models.entity.bibleModels.BibleAudioPropertyResponseData
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleVersePropertyData
import com.oyetech.models.entity.contentProperties.AudioDurationModel
import com.oyetech.models.entity.contentProperties.ContentWrapperClass
import com.oyetech.models.entity.contentProperties.OldContentPlayerDetailsModel
import com.oyetech.models.entity.language.TextResourcesDataResponse
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.alarm.DataAlarmModel
import com.oyetech.models.utils.states.ReadPageOrientation
import com.oyetech.models.utils.states.TextFontEnum

/**
Created by Erdi Özbek
-6.10.2022-
-15:50-
 **/

interface SharedHelperRepository {

    fun getTotalAppOpenCount(): Int
    fun increaseAppOpenCount()
    fun isReviewAlreadyShown(): Boolean
    fun setReviewAlreadyShown(status: Boolean)
    fun getUserDontWantSeeFlagTimeExpired(): Boolean
    fun getReviewUserDontWantSee(): Boolean
    fun setReviewUserDontWantSee(status: Boolean)
    fun isSubsDialogCanShow(): Boolean
    fun putDateWhenSubsDialogShow()
    fun getIsDateWhenSubsDialogShow(): Boolean

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
    fun autoResumeOnWiredHeadsetConnection(): Boolean
    fun autoResumeOnBluetoothA2dpConnection(): Boolean
    fun pauseWhenNoisy(): Boolean

    fun getSavedLastRadio(): RadioStationResponseData?
    fun saveLastRadioToDB(lastRadioModel: RadioStationResponseData)

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

    fun getContentWrapperModel(): ContentWrapperClass?
    fun saveContentWrapperModel(contentWrapperClass: ContentWrapperClass)
    fun saveContentWrapperModelWithDetails(
        audioDurationModel: AudioDurationModel,
        oldContentPlayerDetailsModel: OldContentPlayerDetailsModel,
    )

    fun getBaseLanguageCode(): String
    fun getBibleContentFontSize(): Int
    fun setBibleContentFontSize(fontSize: Int)
    fun setBibleContentFont(enum: TextFontEnum)
    fun getBibleContentFont(): String
    fun setPageOrientation(type: ReadPageOrientation)
    fun getPageOrientation(): String

    fun setSelectedBibleCountryCode(code: String)
    fun getSelectedBibleCountryCode(): String
    fun setSelectedBibleCode(code: String)
    fun getSelectedBibleCode(): String
    fun setSelectedBibleDataModel(bibleData: BiblePropertyResponseData)
    fun getSelectedBibleDataModel(): BiblePropertyResponseData?
    fun getClientSecretOrProduce(): String
    fun getClientUniqSecretOrProduce(): String
    fun getNotificationToken(): String
    fun putNotificationToken(notificationToken: String)
    fun getLastPlayedAudioData(): BibleAudioPropertyResponseData?
    fun setLastPlayedAudioData(bibleAudioPropertyResponseData: BibleAudioPropertyResponseData)
    fun getSelectedAccent(): String
    fun setSelectedAccent(selectedAccent: String)
    fun getLastPlayedContentPlayerData(): OldContentPlayerDetailsModel?
    fun setLastPlayedContentPlayerData(contentPlayerData: OldContentPlayerDetailsModel)
    fun prepareForNewBibleSelection()

    fun getBookmarkList(): MutableList<BibleVersePropertyData>
    fun addRemoveBookmark(
        responseData: BibleVersePropertyData,
        isAdd: Boolean,
    ): MutableList<BibleVersePropertyData>

    fun putNotificationSettingsData(notificationSettingsData: NotificationSettingsData)
    fun getNotificationSettingsData(): NotificationSettingsData

    fun getLocalNotificationVerseList(): List<BibleVersePropertyData>
    fun putLocalNotificationVerseList(list: List<BibleVersePropertyData>?)

    fun getLocalNotificationPrayList(): List<String>
    fun putLocalNotificationPrayList(list: List<String>?)

    fun putLastShownAlarmPray(prayContent: String)
    fun putLastShownAlarmVerse(verse: BibleVersePropertyData)

    fun getLastShownAlarmPray(): String?
    fun getLastShownAlarmVerse(): BibleVersePropertyData?

    fun getLastFeedbackTimeMilis(): Long
    fun canUserSendFeedback(): Boolean
    fun setLastFeedbackTimeMilis()

    fun getUserSignInLocally(): Boolean
    fun setUserSignInLocally(isLoggedIn: Boolean)
    fun saveAlarm(dataAlarmModel: DataAlarmModel)
    fun getAlarm(): DataAlarmModel?
}
