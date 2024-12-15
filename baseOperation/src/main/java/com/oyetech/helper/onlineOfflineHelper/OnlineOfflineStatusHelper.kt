package com.oyetech.helper.onlineOfflineHelper

import com.oyetech.core.coroutineHelper.globalScopeOnEachTryCatch
import com.oyetech.domain.useCases.DatabaseOperationUseCase
import com.oyetech.models.entity.general.IsOnlineBaseData
import com.oyetech.models.entity.user.UserOnlineOfflineStatusData
import timber.log.Timber

/**
Created by Erdi Özbek
-4.06.2022-
-15:39-
 **/

class OnlineOfflineStatusHelper(private var databaseOperationUseCase: DatabaseOperationUseCase) {

    private var onlineOfflineUserList = HashMap<Long, Boolean>()

    // key-> screen name
    // value-> value
    private var currentOnlineStatusList = HashMap<String, List<IsOnlineBaseData>>()

    init {
        observeOnlineOfflineChange()
    }

    private fun observeOnlineOfflineChange() {
        databaseOperationUseCase.onUserOnlineOfflineStatusChannel().globalScopeOnEachTryCatch {
            // Timber.d("it === " + it)
            addToUserListToStatus(it)
            findAndChangeAdapterItemOnlineStatus(it)
        }
    }

    private fun addToUserListToStatus(userOnlineOfflineStatusData: UserOnlineOfflineStatusData) {
        onlineOfflineUserList[userOnlineOfflineStatusData.userId] =
            userOnlineOfflineStatusData.isOnline
    }

    private fun findAndChangeAdapterItemOnlineStatus(itemm: UserOnlineOfflineStatusData) {
        currentOnlineStatusList.forEach { mapp ->

            var currrentList = mapp.value ?: listOf()
            if (currrentList.size == 0) {
                return
            }
            currrentList.forEach {
                if (it.baseUserId == itemm.userId) {

                    it.isOnlineView = itemm.isOnline
                }
            }
        }
    }

    fun detachAdapter(screenName: String) {
        currentOnlineStatusList.remove(screenName)
    }

    fun initDataList(dataListLiveData: List<*>?, screenName: String) {
        if (dataListLiveData.isNullOrEmpty()) {
            return
        }

        var dataaa = dataListLiveData as? List<IsOnlineBaseData> ?: listOf()
        if (dataaa.isNullOrEmpty()) {
            Timber.d("dataaa null")
            return
        }

        dataaa.map {
            if (onlineOfflineUserList[it.baseUserId] != null) {
                it.isOnlineView = onlineOfflineUserList[it.baseUserId]!!
            }
        }
        currentOnlineStatusList[screenName] = dataaa
    }
    /*
    fun initDataListMessageConversatiıb(dataListLiveData: List<MessageConversationDataResponse>, screenName: String) {
        Timber.d("initt data  == " + dataListLiveData.toString())

        dataListLiveData.map { dataResponse ->
            var it = dataResponse.isOnlineBaseDataa
            if (it == null) {
                Timber.d("problemmmm")
            } else {
                Timber.d("elseeeE == " + it.baseUserId)

                if (onlineOfflineUserList[it.baseUserId] != null) {
                    Timber.d("change listt")
                    it.isOnlineView = onlineOfflineUserList[it.baseUserId]!!
                }
            }
        }
        currentOnlineStatusList[screenName] = buildList {
            dataListLiveData.forEach {
                this.add(it.isOnlineBaseDataa!!)
            }
        }
    }

     */
}

object IsOnlineStatusScreenName {
    const val Feed_Screen = "Feed_Screen"
    const val Message_Screen = "Message_Screen"
    const val Message_Detail_Screen = "Message_Detail_Screen"
    const val Profile_Screen = "Profile_Screen"
}
