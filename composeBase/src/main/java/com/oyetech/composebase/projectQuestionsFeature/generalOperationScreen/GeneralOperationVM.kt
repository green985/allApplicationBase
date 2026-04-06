package com.oyetech.composebase.projectQuestionsFeature.generalOperationScreen

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.experimental.authOperation.AuthOperationVM
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.domain.repository.NotificationHandlerRepository
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.loginOperation.AuthOperationRepository
import com.oyetech.domain.useCases.AnswerUseCase
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.helpers.AppReviewOperationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-15.12.2024-
-14:05-
 **/

@Suppress("LongParameterList")
class GeneralOperationVM(
    appDispatchers: AppDispatchers,
    private val appReviewOperationUseCase: AppReviewOperationUseCase,
    private val sharedHelperRepository: SharedOperationRepository,
    private val authOperationRepository: AuthOperationRepository,
    private val authOperationVM: AuthOperationVM,
    private val answerUseCase: AnswerUseCase,
    private val notificationHandlerRepository: NotificationHandlerRepository,
    private val snackbarDelegate: SnackbarDelegate,
    private val navigationUseCase: NavigationUseCase,
) : BaseViewModel(appDispatchers) {

    fun getReviewCanShowState() = appReviewOperationUseCase.getReviewCanShowState()

    fun getReviewOperationStatus() = appReviewOperationUseCase.getReviewOperationStateFlow()

    fun dismissReviewState() {
        getReviewCanShowState().value = false
        sharedHelperRepository.setReviewAlreadyShown(true)
    }

    fun startReviewOperation() {
        getReviewCanShowState().value = false
        appReviewOperationUseCase.startAppReviewOperation()
    }

    fun dismissDialog() {
        getReviewCanShowState().value = false
        sharedHelperRepository.setReviewAlreadyShown(true)
    }

    fun observeRealtimeMessages() {}

    init {
//        messageOperationVM.initFun()
        sharedHelperRepository.increaseAppOpenCount()
        autoLogin()
        viewModelScope.launch(getDispatcherIo()) {
            delay(1000)
            appReviewOperationUseCase.controlReviewCanShow()
        }
        getUserAnswers()
        observeFormNotifications()
    }

    private fun autoLogin() {
        viewModelScope.launch(getDispatcherIo()) {
            val savedUser = sharedHelperRepository.getGoogleUserData()
            if (savedUser == null || savedUser.token.isBlank()) {
                authOperationRepository.logoutAndClearSession()
                return@launch
            }

            authOperationRepository.syncUserFromSavedSession().fold(
                onSuccess = { syncedUser ->
                    Timber.d("GeneralOperationVM autoLogin success: userId=${syncedUser.userId}")
                },
                onFailure = { error ->
                    Timber.d("GeneralOperationVM autoLogin error: ${error.message}")
                    if (error.message?.contains("401", ignoreCase = true) == true) {
                        authOperationRepository.logoutAndClearSession()
                    }
                }
            )
        }
    }

    private fun observeFormNotifications() {
        viewModelScope.launch(getDispatcherIo()) {
            notificationHandlerRepository.formNotificationFlow.collectLatest { data ->
                Timber.d("GeneralOperationVM: form notification received -> formId=${data.formId}")
                snackbarDelegate.triggerSnackbarState(
                    message = LanguageKey.formResultReadyMessage,
                    actionLabel = LanguageKey.viewText,
                    onAction = {
                        navigationUseCase.navigateTo(
                            AppRoute.QuestionFormScreen(formId = data.formId)
                        )
                    }
                )
            }
        }
    }

    private fun getUserAnswers() {
        var x: Job? = null
        x = viewModelScope.launch(getDispatcherIo()) {
            authOperationVM.authOperationState.collectLatest {
                if (it.isLogin) {
                    answerUseCase.getAnswersByUser(it.userId)
                        .asResult()
                        .collectLatest {
                            /* repo updates its own state */
                            x?.cancel()
                        }
                }
            }
        }
    }
}
