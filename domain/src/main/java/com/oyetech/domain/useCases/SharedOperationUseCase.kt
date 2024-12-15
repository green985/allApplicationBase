package com.oyetech.domain.useCases

import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.models.entity.NotificationSettingsData
import com.oyetech.models.entity.auth.AuthRequestResponse
import com.oyetech.models.entity.bibleModels.BibleAudioPropertyResponseData
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleVersePropertyData
import com.oyetech.models.entity.contentProperties.OldContentPlayerDetailsModel
import com.oyetech.models.entity.language.TextResourcesDataResponse
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import com.oyetech.models.radioProject.helperModels.alarm.DataAlarmModel
import com.oyetech.models.utils.states.ReadPageOrientation
import com.oyetech.models.utils.states.TextFontEnum

/**
Created by Erdi Özbek
-21.11.2022-
-18:25-
 **/

class SharedOperationUseCase(private val repository: SharedHelperRepository) {

    fun getTotalAppOpenCount(): Int {
        return repository.getTotalAppOpenCount()
    }

    fun increaseAppOpenCount() {
        repository.increaseAppOpenCount()
    }

    fun isReviewAlreadyShown(): Boolean {
        return repository.isReviewAlreadyShown()
    }

    fun setReviewAlreadyShown(status: Boolean) {
        repository.setReviewAlreadyShown(status)
    }

    fun getUserDontWantSeeFlagTimeExpired(): Boolean {
        return repository.getUserDontWantSeeFlagTimeExpired()
    }

    fun getReviewUserDontWantSee(): Boolean {
        return repository.getReviewUserDontWantSee()
    }

    fun setReviewUserDontWantSee(status: Boolean) {
        repository.setReviewUserDontWantSee(status)
    }

    fun isSubsDialogCanShow(): Boolean {
        return repository.isSubsDialogCanShow()
    }

    fun putDateWhenSubsDialogShow() {
        repository.putDateWhenSubsDialogShow()
    }

    fun getIsDateWhenSubsDialogShow(): Boolean {
        return repository.getIsDateWhenSubsDialogShow()
    }

    fun getToken(): String {
        return repository.getToken()
    }

    fun isLogin(): Boolean {
        return repository.isLogin()
    }

    fun getRefreshToken(): String {
        return repository.getRefreshToken()
    }

    fun getUserId(): Long {
        return repository.getUserId()
    }

    fun isUserLogin(): Boolean {
        return repository.isUserLogin()
    }

    fun getUserAuthModel(): AuthRequestResponse? {
        return repository.getUserAuthModel()
    }

    fun saveUserTokenData(tokenData: AuthRequestResponse?) {
        repository.saveUserTokenData(tokenData)
    }

    fun getUserNickname(): String {
        return repository.getUserNickname()
    }

    fun getLastGuid(): String {
        return repository.getLastGuid()
    }

    fun getLanguageValueOrNull(): TextResourcesDataResponse? {
        return repository.getLanguageValueOrNull()
    }

    fun getLanguageTimeValue(): Long {
        return repository.getLanguageTimeValue()
    }

    fun getLastClientUniqId(): String {
        return repository.getLastClientUniqId()
    }

    fun removeSharedPrefValues() {
        repository.removeSharedPrefValues()
    }

    fun controlIsKeyNotNull(key: String): Boolean {
        return repository.controlIsKeyNotNull(key)
    }

    fun saveLanguageData(it: TextResourcesDataResponse, withTimeMilis: Long? = null) {
        repository.saveLanguageData(it, withTimeMilis)
    }

    fun autoResumeOnWiredHeadsetConnection(): Boolean {
        return repository.autoResumeOnWiredHeadsetConnection()
    }

    fun autoResumeOnBluetoothA2dpConnection(): Boolean {
        return repository.autoResumeOnBluetoothA2dpConnection()
    }

    fun pauseWhenNoisy(): Boolean {
        return repository.pauseWhenNoisy()
    }

    fun getSavedLastRadio(): RadioStationResponseData? {
        return repository.getSavedLastRadio()
    }

    fun saveLastRadioToDB(lastRadioModel: RadioStationResponseData) {
        repository.saveLastRadioToDB(lastRadioModel)
    }

    fun getStartActionString(): String {
        return repository.getStartActionString()
    }

    fun setStartPageActionString(selectedStartAction: String) {
        repository.setStartPageActionString(selectedStartAction)
    }

    fun getDomainList(): List<String> {
        return repository.getDomainList()
    }

    fun setDomainList(list: List<String>) {
        repository.setDomainList(list)
    }

    fun setFirstOpenApplicationTimeMilis(timeMilis: Long) {
        repository.setFirstOpenApplicationTimeMilis(timeMilis)
    }

    fun getFirstOpenApplicationTimeMilis(): Long {
        return repository.getFirstOpenApplicationTimeMilis()
    }

    fun getFirstAlarmSetTimeMilis(): Long {
        return repository.getFirstAlarmSetTimeMilis()
    }

    fun setFirstAlarmSetTimeMilis() {
        repository.setFirstAlarmSetTimeMilis()
    }

    fun setLastUsageLocalNotificationTextIndex(lastNotificationIndex: Int) {
        repository.setLastUsageLocalNotificationTextIndex(lastNotificationIndex)
    }

    fun getLastUsageLocalNotificationTextIndex(): Int {
        return repository.getLastUsageLocalNotificationTextIndex()
    }

    fun getBaseLanguageCode(): String {
        return repository.getBaseLanguageCode()
    }

    fun getBibleContentFontSize(): Int {
        return repository.getBibleContentFontSize()
    }

    fun setBibleContentFontSize(fontSize: Int) {
        repository.setBibleContentFontSize(fontSize)
    }

    fun getBibleContentFont(): String {
        return repository.getBibleContentFont()
    }

    fun setBibleContentFont(fontEnum: TextFontEnum) {
        repository.setBibleContentFont(fontEnum)
    }

    fun setPageOrientation(type: ReadPageOrientation) {

        repository.setPageOrientation(type)
    }

    fun getPageOrientation(): String {

        return repository.getPageOrientation()
    }

    fun getSelectedBibleCountryCode(): String {

        return repository.getSelectedBibleCountryCode()
    }

    fun setSelectedBibleCountryCode(code: String) {
        repository.setSelectedBibleCountryCode(code = code)
    }

    fun setSelectedBibleCode(code: String) {
        repository.setSelectedBibleCode(code)
    }

    fun getSelectedBibleCode(): String {
        return repository.getSelectedBibleCode()
    }

    fun setSelectedBibleDataModel(bibleData: BiblePropertyResponseData) {
        repository.setSelectedBibleDataModel(bibleData)
    }

    fun prepareForNewBibleSelection() {
        repository.prepareForNewBibleSelection()
    }

    fun getSelectedBibleDataModel(): BiblePropertyResponseData? {
        return repository.getSelectedBibleDataModel()
    }

    fun getClientSecretOrProduce(): String {
        return repository.getClientSecretOrProduce()
    }

    fun getClientUniqSecretOrProduce(): String {
        return repository.getClientUniqSecretOrProduce()
    }

    fun putNotificationToken(token: String) {
        repository.putNotificationToken(token)
    }

    fun getNotificationToken(): String {
        return repository.getNotificationToken()
    }

    fun getLastPlayedAudioData(): BibleAudioPropertyResponseData? {
        return repository.getLastPlayedAudioData()
    }

    fun setLastPlayedAudioData(bibleAudioPropertyResponseData: BibleAudioPropertyResponseData) {
        repository.setLastPlayedAudioData(bibleAudioPropertyResponseData)
    }

    fun getSelectedAccent(): String {

        return repository.getSelectedAccent()
    }

    fun setSelectedAccent(selectedAccent: String) {

        return repository.setSelectedAccent(selectedAccent)
    }

    fun setLastPlayedContentPlayerData(contentPlayerData: OldContentPlayerDetailsModel) {

        repository.setLastPlayedContentPlayerData(contentPlayerData)
    }

    fun getLastPlayedContentPlayerData(): OldContentPlayerDetailsModel? {

        return repository.getLastPlayedContentPlayerData()
    }

    fun saveAlarm(dataAlarmModel: DataAlarmModel) {
        repository.saveAlarm(dataAlarmModel)
    }

    fun getAlarmm(): DataAlarmModel? {
        return repository.getAlarm()
    }

    fun getBookmarkList(): MutableList<BibleVersePropertyData> {
        return repository.getBookmarkList()
    }

    fun addRemoveBookmark(
        responseData: BibleVersePropertyData,
        isAdd: Boolean,
    ): MutableList<BibleVersePropertyData> {
        return repository.addRemoveBookmark(responseData, isAdd)
    }

    fun putNotificationSettingsData(notificationSettingsData: NotificationSettingsData) {
        repository.putNotificationSettingsData(notificationSettingsData)
    }

    fun getNotificationSettingsData(): NotificationSettingsData {
        return repository.getNotificationSettingsData()
    }
}
