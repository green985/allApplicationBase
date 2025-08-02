package com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen.QuoteSettingsEvent.DeleteAccountClick
import com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen.QuoteSettingsEvent.DeleteAccountConfirm
import com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen.QuoteSettingsEvent.DismissDialog
import com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen.QuoteSettingsEvent.OnContactWithMeClicked
import com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen.QuoteSettingsEvent.OnQuoteAdviceScreenClicked
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-25.01.2025-
-18:19-
 **/

class QuoteSettingsVm(
    appDispatchers: AppDispatchers,
    private val loginOperationVM: LoginOperationVM,
    private val navigationUseCase: NavigationUseCase,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(QuoteSettingsUiState())
    val toolbarState = MutableStateFlow(QuoteToolbarState(LanguageKey.settings))

    init {
        viewModelScope.launch(getDispatcherIo()) {
            loginOperationVM.getLoginOperationSharedState().onEach {
                uiState.updateState {
                    copy(isUserLoggedIn = it.isLogin, username = it.displayNameRemote)
                }
            }.collect {}
        }
    }

    override fun onEvent(event: Any) {
        if (event is QuoteSettingsEvent) {
            when (event) {
                DeleteAccountClick -> {
                    uiState.updateState {
                        copy(isDeleteDialogShown = true)
                    }
                }

                OnContactWithMeClicked -> {
                    navigationUseCase.navigate(QuoteAppProjectRoutes.ContactScreen.route)
                }

                DismissDialog -> {
                    uiState.updateState {
                        copy(isDeleteDialogShown = false)
                    }
                }

                DeleteAccountConfirm -> {
                    loginOperationVM.onEvent(LoginOperationEvent.DeleteAccountClick)
                    uiState.updateState {
                        copy(isDeleteDialogShown = false)
                    }
                }

                OnQuoteAdviceScreenClicked -> {
                    navigationUseCase.navigate(QuoteAppProjectRoutes.QuoteAdviceScreen.route)
                }
            }
        }
    }

}