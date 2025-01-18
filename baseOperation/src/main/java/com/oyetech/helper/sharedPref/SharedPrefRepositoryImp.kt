package com.oyetech.helper.sharedPref

import com.google.gson.reflect.TypeToken
import com.oyetech.base.BaseViewModel
import com.oyetech.core.stringOperation.StringHelper
import com.oyetech.cripto.stringKeys.SharedPrefKey
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.helper.language.LanguageHelper
import com.oyetech.models.entity.NotificationSettingsData
import com.oyetech.models.entity.auth.AuthRequestResponse
import com.oyetech.models.entity.auth.TokenDataResponse
import com.oyetech.models.entity.bibleModels.BibleAudioPropertyResponseData
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleVersePropertyData
import com.oyetech.models.entity.contentProperties.AudioDurationModel
import com.oyetech.models.entity.contentProperties.ContentWrapperClass
import com.oyetech.models.entity.contentProperties.OldContentPlayerDetailsModel
import com.oyetech.models.entity.language.TextResourcesDataResponse
import com.oyetech.models.entity.user.UserAlertInfoResponseData
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.alarm.DataAlarmModel
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.helper.TimeFunctions
import com.oyetech.models.utils.states.ReadPageOrientation
import com.oyetech.models.utils.states.TextFontEnum
import timber.log.Timber
import java.util.Calendar

/**
Created by Erdi Özbek
-6.10.2022-
-15:52-
 **/

class SharedPrefRepositoryImp(var sharedHelper: SharedHelper) : SharedHelperRepository {

    override fun getToken(): String {
        var userTokenResponse =
            sharedHelper.retrieveData(SharedPrefKey.USER_TOKEN_MODEL, TokenDataResponse::class.java)

        if (userTokenResponse == null) {
            return ""
        }
        // control token expired.
        /*
        var isTokenExpired = isTokenExpired(userTokenResponse.expires)
        if (isTokenExpired) {
            Timber.d("token expired")
            return ""
        }
         */

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

    fun getUserAlertModelOrNull(): UserAlertInfoResponseData? {
        var userAlertModel =
            sharedHelper.retrieveData(
                SharedPrefKey.USER_ALERT_MODEL,
                UserAlertInfoResponseData::class.java
            )
        return userAlertModel
    }

    fun removeUserAlertModel() {
        sharedHelper.removeData(SharedPrefKey.USER_ALERT_MODEL)
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

    override fun getClientSecretOrProduce(): String {
        var clientSecret = sharedHelper.getStringData(SharedPrefKey.Client_Secret, "")
        if (clientSecret.isNullOrEmpty()) {
            // first open, create new one
            clientSecret = StringHelper.generateRandomGuid()
            sharedHelper.putStringData(SharedPrefKey.Client_Secret, clientSecret)
        }

        return clientSecret
    }

    override fun getClientUniqSecretOrProduce(): String {
        var clientSecret = sharedHelper.getStringData(SharedPrefKey.Client_Secret_Uniq, "")
        if (clientSecret.isNullOrEmpty()) {
            // first open, create new one
            clientSecret = StringHelper.generateRandomGuid()
            sharedHelper.putStringData(SharedPrefKey.Client_Secret_Uniq, clientSecret)
        }

        return clientSecret
    }

    override fun getLanguageValueOrNull(): TextResourcesDataResponse? {
        var textResourcesDataResponse = sharedHelper.retrieveData(
            SharedPrefKey.FirebaseTextResourcesDataResponse,
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

    override fun saveAlarm(dataAlarmModel: DataAlarmModel) {
        sharedHelper.addData(SharedPrefKey.lastAlarmModel, dataAlarmModel)
    }

    override fun getAlarm(): DataAlarmModel? {
        return sharedHelper.retrieveData(SharedPrefKey.lastAlarmModel, DataAlarmModel::class.java)
    }


    override fun prepareForNewBibleSelection() {
        sharedHelper.removeData(SharedPrefKey.CURRENT_BIBLE_CODE)
        sharedHelper.removeData(SharedPrefKey.CURRENT_BIBLE_MODEL)
        sharedHelper.removeData(SharedPrefKey.SELECTED_ACCENT)
        sharedHelper.removeData(SharedPrefKey.LAST_PLAYED_AUDIO_PROPERTY_DATA)
        sharedHelper.removeData(SharedPrefKey.LAST_PLAYED_CONTENT_PLAYER_DETAIL)
        sharedHelper.removeData(SharedPrefKey.LAST_SHOWN_NOTIFICATION_VERSE_ITEM)
        sharedHelper.removeData(SharedPrefKey.LAST_SHOWN_NOTIFICATION_PRAY_ITEM)
        sharedHelper.removeData(SharedPrefKey.Alarm_First_Set_Time_Milis)
        sharedHelper.removeData(SharedPrefKey.LOCAL_NOTIFICATION_PRAY_LIST)
        sharedHelper.removeData(SharedPrefKey.LOCAL_NOTIFICATION_VERSE_LIST)
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

    override fun getBaseLanguageCode(): String {
        var authData = getUserAuthModel()
        var languageCode = authData?.languageCode
        if (languageCode.isNullOrBlank()) {
            var downloadedResourcesDataResponse = sharedHelper.retrieveData(
                SharedPrefKey.FirebaseTextResourcesDataResponse,
                TextResourcesDataResponse::class.java
            )
            if (downloadedResourcesDataResponse != null) {
                if (downloadedResourcesDataResponse.languageCode.isNotBlank()) {
                    languageCode = downloadedResourcesDataResponse.languageCode
                        ?: LanguageHelper.getLocalLanguageCode()
                } else {
                    languageCode = LanguageHelper.getLocalLanguageCode()
                }
            } else {
                // first init language code
                languageCode = LanguageHelper.getLocalLanguageCode()
            }
        }
        if (languageCode.isNullOrBlank()) {
            languageCode = "en"
        }

        return languageCode
    }

    override fun saveLanguageData(it: TextResourcesDataResponse, withTimeMilis: Long?) {
        sharedHelper.addData(SharedPrefKey.FirebaseTextResourcesDataResponse, it)
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

    fun getSharedHelperBase(): SharedHelper {
        return sharedHelper
    }

    fun setReviewDontWantSee(status: Boolean) {

        var timeMilis = Calendar.getInstance().timeInMillis
        sharedHelper.putLongData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE_TIME, timeMilis)
        sharedHelper.putBooleanData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE, status)
    }

    fun isMessageTranslationInformationGrant(): Boolean {
        return sharedHelper.getBooleanData(
            SharedPrefKey.IS_MESSAGE_TRANSLATE_INFO_MESSAGE_SHOW,
            false
        )
    }

    fun putMessageTranslationInformationGrant() {
        sharedHelper.putBooleanData(SharedPrefKey.IS_MESSAGE_TRANSLATE_INFO_MESSAGE_SHOW, true)
    }

    fun getUserFeedListVisibility(): Boolean {
        return sharedHelper.getBooleanData(SharedPrefKey.FEED_LIST_VISIBILITY, true)
    }

    fun setUserFeedListVisibility(visible: Boolean) {
        sharedHelper.putBooleanData(SharedPrefKey.FEED_LIST_VISIBILITY, visible)
    }

    // TODO will be fix later...
    fun setNotificationPermissionDenied(isDenied: Boolean) {
    }

    override fun getTotalAppOpenCount(): Int {
        var openCount = sharedHelper.getIntData(SharedPrefKey.APP_FIRST_OPEN_COUNT, 0)

        return openCount
    }

    override fun increaseAppOpenCount() {
        var openCount = sharedHelper.getIntData(SharedPrefKey.APP_FIRST_OPEN_COUNT, 0)

        openCount += 1

        sharedHelper.putIntData(SharedPrefKey.APP_FIRST_OPEN_COUNT, openCount)
    }

    override fun isReviewAlreadyShown(): Boolean {
        var isReviewAlreadyShown =
            sharedHelper.getBooleanData(SharedPrefKey.IS_REVIEW_ALREADY_SHOWN, false)
        return isReviewAlreadyShown
    }

    override fun setReviewAlreadyShown(status: Boolean) {
        sharedHelper.putBooleanData(SharedPrefKey.IS_REVIEW_ALREADY_SHOWN, status)
    }

    override fun setReviewUserDontWantSee(status: Boolean) {
        var timeMilis = Calendar.getInstance().timeInMillis
        sharedHelper.putLongData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE_TIME, timeMilis)
        sharedHelper.putBooleanData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE, status)
    }

    override fun isSubsDialogCanShow(): Boolean {
        var timeMilis = sharedHelper.getLongData(SharedPrefKey.SUBS_DIALOG_SHOWN_TIME_MILIS, 0L)
        if (timeMilis == 0L) {
            Timber.d("totalAppCopunt first init")
            return false
        }

        var isTimeExpired =
            TimeFunctions.isTimeExpiredFromParamHourWithMilis(
                timeMilis,
                HelperConstant.SUBS_DIALOG_RESOW_USER_EXPIRED_TIME
            )

        if (isTimeExpired) {
            putDateWhenSubsDialogShow()
            Timber.d("totalAppCopunt expried, can showwww")
            return true
        }

        Timber.d("totalAppCopunt not expiredddd")
        return false
    }

    override fun getIsDateWhenSubsDialogShow(): Boolean {
        var timeMilis = sharedHelper.getLongData(SharedPrefKey.SUBS_DIALOG_SHOWN_TIME_MILIS, 0L)
        if (timeMilis == 0L) {
            Timber.d("totalAppCopunt first init")
            return false
        } else {
            return true
        }
    }

    override fun putDateWhenSubsDialogShow() {
        var timeMilis = Calendar.getInstance().timeInMillis
        sharedHelper.putLongData(SharedPrefKey.SUBS_DIALOG_SHOWN_TIME_MILIS, timeMilis)
    }

    override fun getReviewUserDontWantSee(): Boolean {
        var userDontWantSeeFlag =
            sharedHelper.getBooleanData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE, false)

        if (!userDontWantSeeFlag) {
            Timber.d("user not select dont want see flag")
            return false
        }

        if (userDontWantSeeFlag) {
            var userDontWantSeeFlagTimeExpired = getUserDontWantSeeFlagTimeExpired()
            if (userDontWantSeeFlagTimeExpired) {
                Timber.d("time expriredd show dialog again")
                return false
            }
        }

        Timber.d("user dont see dialog for review")
        return true
    }

    override fun getUserDontWantSeeFlagTimeExpired(): Boolean {
        var time = sharedHelper.getLongData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE_TIME, -1)
        if (time == -1L) {
            Timber.d("first init, can show")
            return true
        }
        var isTimeExpired = TimeFunctions.isTimeExpiredFromParamHourWithMilis(
            time,
            HelperConstant.REVIEW_ASK_EXPIRED_TIME
        )

        if (isTimeExpired) {
            sharedHelper.removeData(SharedPrefKey.IS_REVIEW_USER_DONT_WANT_SEE_TIME)
            Timber.d("time expried, can showwww")
            return true
        }

        Timber.d("time not expiredddd")
        return false
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

    override fun getContentWrapperModel(): ContentWrapperClass? {
        return sharedHelper.retrieveData(
            SharedPrefKey.Content_Wrapper_Model_Key,
            ContentWrapperClass::class.java
        )
    }

    override fun saveContentWrapperModel(contentWrapperClass: ContentWrapperClass) {
        sharedHelper.addData(SharedPrefKey.Content_Wrapper_Model_Key, contentWrapperClass)
    }

    override fun saveContentWrapperModelWithDetails(
        audioDurationModel: AudioDurationModel,
        oldContentPlayerDetailsModel: OldContentPlayerDetailsModel,
    ) {
        sharedHelper.addData(
            SharedPrefKey.Content_Wrapper_Model_Key,
            ContentWrapperClass(audioDurationModel, oldContentPlayerDetailsModel)
        )
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

    override fun getBibleContentFontSize(): Int {
        return sharedHelper.getIntData(SharedPrefKey.BIBLE_CONTENT_FONT_SIZE, 10)
    }

    override fun setBibleContentFontSize(fontSize: Int) {
        return sharedHelper.putIntData(SharedPrefKey.BIBLE_CONTENT_FONT_SIZE, fontSize)
    }

    override fun getBibleContentFont(): String {

        var fontEnum = sharedHelper.getStringData(
            SharedPrefKey.BIBLE_CONTENT_FONT,
            TextFontEnum.DEFAULT.type
        ) ?: TextFontEnum.DEFAULT.type

        return fontEnum
    }

    override fun setBibleContentFont(enum: TextFontEnum) {
        return sharedHelper.putStringData(SharedPrefKey.BIBLE_CONTENT_FONT, enum.type)
    }

    override fun setPageOrientation(type: ReadPageOrientation) {
        sharedHelper.putStringData(SharedPrefKey.READ_PAGE_ORIENTATION, type.type)
    }

    override fun getPageOrientation(): String {
        return sharedHelper.getStringData(
            SharedPrefKey.READ_PAGE_ORIENTATION,
            ReadPageOrientation.VERTICAL.type
        ) ?: ReadPageOrientation.VERTICAL.type
    }

    override fun setSelectedBibleCountryCode(code: String) {
        sharedHelper.putStringData(SharedPrefKey.LAST_SELECTED_COUNTRY_CODE, code)
    }

    override fun getSelectedBibleCountryCode(): String {
        return sharedHelper.getStringData(
            SharedPrefKey.LAST_SELECTED_COUNTRY_CODE,
            ""
        ) ?: ""
    }

    override fun setSelectedBibleCode(code: String) {
        sharedHelper.putStringData(SharedPrefKey.CURRENT_BIBLE_CODE, code)
    }

    override fun getSelectedBibleCode(): String {
        return sharedHelper.getStringData(
            SharedPrefKey.CURRENT_BIBLE_CODE,
            ""
        ) ?: ""
    }

    override fun setSelectedBibleDataModel(bibleData: BiblePropertyResponseData) {
        sharedHelper.addData(SharedPrefKey.CURRENT_BIBLE_MODEL, bibleData)
    }

    override fun getSelectedBibleDataModel(): BiblePropertyResponseData? {
        return sharedHelper.retrieveData(
            SharedPrefKey.CURRENT_BIBLE_MODEL,
            BiblePropertyResponseData::class.java
        )
    }

    override fun putNotificationToken(notificationToken: String) {
        sharedHelper.putStringData(SharedPrefKey.LAST_NOTIFICATION_TOKEN, notificationToken)
    }

    override fun getNotificationToken(): String {
        return sharedHelper.getStringData(
            SharedPrefKey.LAST_NOTIFICATION_TOKEN,
            ""
        ) ?: ""
    }

    override fun getLastPlayedAudioData(): BibleAudioPropertyResponseData? {
        return sharedHelper.retrieveData(
            SharedPrefKey.LAST_PLAYED_AUDIO_PROPERTY_DATA,
            BibleAudioPropertyResponseData::class.java
        ) ?: null
    }

    override fun setLastPlayedAudioData(bibleAudioPropertyResponseData: BibleAudioPropertyResponseData) {
        sharedHelper.addData(
            SharedPrefKey.LAST_PLAYED_AUDIO_PROPERTY_DATA,
            bibleAudioPropertyResponseData
        )
    }

    override fun getLastPlayedContentPlayerData(): OldContentPlayerDetailsModel? {
        return sharedHelper.retrieveData(
            SharedPrefKey.LAST_PLAYED_CONTENT_PLAYER_DETAIL,
            OldContentPlayerDetailsModel::class.java
        ) ?: null
    }

    override fun setLastPlayedContentPlayerData(contentPlayerData: OldContentPlayerDetailsModel) {
        sharedHelper.addData(
            SharedPrefKey.LAST_PLAYED_CONTENT_PLAYER_DETAIL,
            contentPlayerData
        )
    }

    override fun getSelectedAccent(): String {
        return sharedHelper.getStringData(
            SharedPrefKey.SELECTED_ACCENT,
            ""
        ) ?: ""
    }

    override fun setSelectedAccent(selectedAccent: String) {
        sharedHelper.putStringData(
            SharedPrefKey.SELECTED_ACCENT,
            selectedAccent
        )
    }

    override fun getBookmarkList(): MutableList<BibleVersePropertyData> {
        val typeToken = object : TypeToken<List<BibleVersePropertyData>>() {}
        var list = sharedHelper.retrieveListData(SharedPrefKey.BOOKMARK_LIST, typeToken)

        if (list.isNullOrEmpty()) {
            return arrayListOf<BibleVersePropertyData>()
        }

        return list.toMutableList()
    }

    private fun setBookmarkList(list: MutableList<BibleVersePropertyData>) {
        var distinctList = list.distinctBy { it.verseId }
        sharedHelper.addListData(SharedPrefKey.BOOKMARK_LIST, distinctList)
    }

    override fun addRemoveBookmark(
        responseData: BibleVersePropertyData,
        isAdd: Boolean,
    ): MutableList<BibleVersePropertyData> {
        val typeToken = object : TypeToken<List<BibleVersePropertyData>>() {}
        var bookmarkList = getBookmarkList()

        if (isAdd) {
            bookmarkList.add(responseData)
        } else {
            bookmarkList.remove(responseData)
        }

        setBookmarkList(bookmarkList)
        return bookmarkList
    }

    override fun putNotificationSettingsData(notificationSettingsData: NotificationSettingsData) {
        sharedHelper.addData(SharedPrefKey.NOTIFICATION_SETTINGS_DATA, notificationSettingsData)
    }

    override fun getNotificationSettingsData(): NotificationSettingsData {

        var notificationSettingsData = sharedHelper.retrieveData(
            SharedPrefKey.NOTIFICATION_SETTINGS_DATA,
            NotificationSettingsData::class.java
        )

        Timber.d("notificationSettingsData == " + notificationSettingsData.toString())
        if (notificationSettingsData == null) {
            notificationSettingsData = NotificationSettingsData()
        }

        return notificationSettingsData
    }

    override fun getLocalNotificationVerseList(): List<BibleVersePropertyData> {
        val typeToken = object : TypeToken<List<BibleVersePropertyData>>() {}

        var localNotificationList =
            sharedHelper.retrieveListData(SharedPrefKey.LOCAL_NOTIFICATION_VERSE_LIST, typeToken)

        return localNotificationList ?: emptyList()
    }

    override fun putLocalNotificationVerseList(list: List<BibleVersePropertyData>?) {
        if (list.isNullOrEmpty()) {
            return
        }
        sharedHelper.addListData(SharedPrefKey.LOCAL_NOTIFICATION_VERSE_LIST, list)

    }

    override fun getLocalNotificationPrayList(): List<String> {
        val typeToken = object : TypeToken<List<String>>() {}

        var localNotificationList =
            sharedHelper.retrieveListData(SharedPrefKey.LOCAL_NOTIFICATION_PRAY_LIST, typeToken)

        return localNotificationList ?: emptyList()
    }

    override fun putLocalNotificationPrayList(list: List<String>?) {
        sharedHelper.addListData(SharedPrefKey.LOCAL_NOTIFICATION_PRAY_LIST, list)
    }

    override fun putLastShownAlarmPray(prayContent: String) {
        sharedHelper.addData(SharedPrefKey.LAST_SHOWN_NOTIFICATION_PRAY_ITEM, prayContent)

    }

    override fun putLastShownAlarmVerse(verse: BibleVersePropertyData) {
        sharedHelper.addData(SharedPrefKey.LAST_SHOWN_NOTIFICATION_VERSE_ITEM, verse)
    }

    override fun getLastShownAlarmPray(): String? {
        return sharedHelper.getStringData(SharedPrefKey.LAST_SHOWN_NOTIFICATION_PRAY_ITEM, null)
    }

    override fun getLastShownAlarmVerse(): BibleVersePropertyData? {
        return sharedHelper.retrieveData(
            SharedPrefKey.LAST_SHOWN_NOTIFICATION_VERSE_ITEM,
            BibleVersePropertyData::class.java
        )

    }

    override fun setLastFeedbackTimeMilis() {
        val timeMilis = System.currentTimeMillis()
        sharedHelper.putLongData(SharedPrefKey.LAST_FEEDBACK_TIME_MILIS, timeMilis)
    }

    override fun getLastFeedbackTimeMilis(): Long {
        return sharedHelper.getLongData(SharedPrefKey.LAST_FEEDBACK_TIME_MILIS, 0L) ?: 0L
    }

    override fun canUserSendFeedback(): Boolean {
        val lastFeedbackTime = getLastFeedbackTimeMilis()
        if (lastFeedbackTime == 0L) {
            return true
        }
        val isExpired = TimeFunctions.isTimeExpiredFromParamHourWithMilis(
            lastFeedbackTime,
            6
        )

        Timber.d("isExpired == $isExpired")
        return isExpired
    }

    override fun getUserSignInLocally(): Boolean {
        return sharedHelper.getBooleanData(SharedPrefKey.IS_USER_SIGN_IN_WITH_GOOGLE, false)
    }

    override fun setUserSignInLocally(isSignIn: Boolean) {
        sharedHelper.putBooleanData(SharedPrefKey.IS_USER_SIGN_IN_WITH_GOOGLE, isSignIn)
    }
}
