package com.example.settingss.ui.contactWithUs;

import androidx.lifecycle.viewModelScope
import com.oyetech.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.domain.repository.helpers.FirebaseDBOperationRepository
import com.oyetech.languageModule.keyset.WallpaperLanguage
import com.oyetech.models.utils.states.ViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-07/04/2024-
-16:11-
 **/

class ContactWithUsVM(
    appDispatchers: AppDispatchers,
    private val firebaseDBOperationRepository: FirebaseDBOperationRepository,
) : BaseViewModel(appDispatchers) {

    var feedbackSendOperationSingleLiveEvent = makeSingleLiveData<Boolean>()

    init {
        observeFeedbackSendOperation()
    }

    private fun observeFeedbackSendOperation() {
        viewModelScope.launch(dispatcher.io) {
            firebaseDBOperationRepository.sendFeedbackOperationStateFlow.collectLatest {
                if (it == null) return@collectLatest
                if (it) {
                    delay(250)
                    feedbackSendOperationSingleLiveEvent.postValue(ViewState.success())
                    sharedPrefRepositoryHelper.setLastFeedbackTimeMilis()
                } else {
                    feedbackSendOperationSingleLiveEvent.postValue(ViewState.error(WallpaperLanguage.DEFAULT_ERROR))
                }
                firebaseDBOperationRepository.clearFeedbackOperationStateFlow()
            }
        }
    }

    fun sendFeedback(emailString: String, feedbackHeader: String, feedbackBody: String) {
        val userCanSendFeedback = sharedPrefRepositoryHelper.canUserSendFeedback()

        if (!userCanSendFeedback) {
            feedbackSendOperationSingleLiveEvent.value =
                ViewState.error(msg = WallpaperLanguage.FEEDBACK_ALREADY_SEND)
            return
        }

        feedbackSendOperationSingleLiveEvent.value = ViewState.loading()

        firebaseDBOperationRepository.sendFeedback(emailString, feedbackHeader, feedbackBody)
    }
}