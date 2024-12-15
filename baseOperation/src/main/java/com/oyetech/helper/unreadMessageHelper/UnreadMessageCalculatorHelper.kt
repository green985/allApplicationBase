package com.oyetech.helper.unreadMessageHelper

import androidx.lifecycle.MutableLiveData
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.entity.unreadCount.UnreadMessagesData
import timber.log.Timber

/**
Created by Erdi Özbek
-23.04.2022-
-12:44-
 **/

class UnreadMessageCalculatorHelper {

    var totalUnreadConversationCountLiveData = MutableLiveData<Int>()
    var totalNotificationCountLiveData = MutableLiveData<Int>()
    var conversationUnreadCountLiveData = MutableLiveData<List<UnreadMessagesData>>()

    var notificationCountHashMap = HashMap<String, Int>()

    var messageDetailUserName = ""
    var messageDetailConversation = 0L

    fun initUnreadMessageChange(unreadMessageList: List<UnreadMessagesData>) {
        var tmpList = removeUnreadForOpenConversation(unreadMessageList)

        if (unreadMessageList.isNullOrEmpty()) {
            Timber.d("unreadMessageList null or empty")
            conversationUnreadCountLiveData.postValue(listOf())
            totalUnreadConversationCountLiveData.postValue(0)
            return
        }
        calculateUnreadConversationCount(tmpList)
        triggerUnreadConversationCountLiveData(tmpList)
    }

    private fun removeUnreadForOpenConversation(list: List<UnreadMessagesData>): MutableList<UnreadMessagesData> {
        var data = list.find {
            it.conversationId == messageDetailConversation
        }
        var returnList = list.toMutableList()
        returnList.remove(data)
        return returnList
    }

    private fun triggerUnreadConversationCountLiveData(unreadMessageList: List<UnreadMessagesData>) {
        conversationUnreadCountLiveData.postValue(unreadMessageList)
    }

    fun getLastUnreadCountList(): List<UnreadMessagesData> {
        return conversationUnreadCountLiveData.value ?: listOf()
    }

    private fun calculateTotalUnreadCount(unreadMessageList: List<UnreadMessagesData>): Int {
        var totalCount = 0
        unreadMessageList.forEach {
            totalCount += it.unreadMessageCount
        }
        //  Timber.d("totalCount == " + totalCount)
        totalUnreadConversationCountLiveData.postValue(totalCount)
        return totalCount
    }

    private fun calculateUnreadConversationCount(unreadMessageList: List<UnreadMessagesData>): Int {
        //  Timber.d("calculatee == " + unreadMessageList.size)

        var totalCount = 0
        totalUnreadConversationCountLiveData.postValue(unreadMessageList.size)
        return totalCount
    }

    fun addNewMessageToNotificationMessageCountList(messageDetailDataResponse: MessageDetailDataResponse) {
        var nick = messageDetailDataResponse.fromUserNick
        var messages = notificationCountHashMap[nick] ?: 0
        notificationCountHashMap[nick] = messages + 1
    }

    fun removeToNotificationMessageCountList(nickname: String) {
        notificationCountHashMap.remove(nickname)
    }

    fun getNotificationMessagesTotalCount(): Int {
        var totalCount = 0
        notificationCountHashMap.values.forEach {
            totalCount += it
        }

        return totalCount
    }

    fun setBadgeForNotificationTab() {
        var count = totalNotificationCountLiveData.value ?: 0
        count += 1
        totalNotificationCountLiveData.postValue(count)
    }

    fun setNotificationBadgeClear() {
        totalNotificationCountLiveData.postValue(0)
    }
}
