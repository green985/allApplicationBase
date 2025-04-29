package com.oyetech.composebase.sharedScreens.messaging

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.domain.repository.firebase.realtime.FirebaseRealtimeHelperRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-4.03.2025-
-20:54-
 **/

class MessageOperationVM(
    appDispatchers: AppDispatchers,
    private val firebaseRealtimeHelperRepository: FirebaseRealtimeHelperRepository,
) : BaseViewModel(appDispatchers) {

    var observeMessageJob: Job? = null

    init {
        Timber.d("MessageOperationVM init")
        observeRealtimeOperation()
    }

    fun observeRealtimeOperation() {
        if (GeneralSettings.isRealTimeOperationEnable().not()) {
            Timber.d("Real time operation is disabled")
            return
        }

        observeMessageJob?.cancel()
        observeMessageJob = viewModelScope.launch(getDispatcherIo()) {
            firebaseRealtimeHelperRepository.observeUserMessagesRealtimeOperations().asResult()
                .collectLatest {
                    it.fold(
                        onSuccess = {}, onFailure = {
                            delay(1000)
                            observeRealtimeOperation()
                        })
                }
        }
    }

    fun initFun(): String {
        return this.javaClass.name
    }

}