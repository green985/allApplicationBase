package com.oyetech.composebase.sharedScreens.settings

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.loginOperations.LoginOperationEvent
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.cripto.stringKeys.WebSiteUrls
import com.oyetech.domain.repository.firebase.FirebaseUserRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.contextHelper.UrlHelper
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-25.03.2025-
-21:51-
 **/

class FacSettingsVm(
    appDispatchers: AppDispatchers,
    private val loginOperationVM: LoginOperationVM,
    private val navigationUseCase: com.oyetech.domain.useCases.NavigationUseCase,
    private val firebaseUserRepository: FirebaseUserRepository,
) : BaseViewModel(appDispatchers) {

    val toolbarState = MutableStateFlow(QuoteToolbarState(LanguageKey.settings))
    val uiState =
        MutableStateFlow(FacSettingsUiState())

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
        if (event is FacSettingsUiEvent) {
            when (event) {
                FacSettingsUiEvent.ContactClicked -> {
                    navigationUseCase.navigate(QuoteAppProjectRoutes.ContactScreen.route)
                }

                FacSettingsUiEvent.InfoClicked -> {
                    uiState.updateState {
                        copy(isInfoDialogShown = true)
                    }
                }

                FacSettingsUiEvent.InfoDialogDismissed -> {
                    uiState.updateState {
                        copy(isInfoDialogShown = false)
                    }
                }

                FacSettingsUiEvent.PrivacyPolicyClicked -> {
                    UrlHelper.openUrl(context, WebSiteUrls.Fac_Privacy_Policy_URL)
                }

                FacSettingsUiEvent.TermsAndConditionsClicked -> {
                    UrlHelper.openUrl(context, WebSiteUrls.Fac_Terms_Conditions_URL)

                }

                FacSettingsUiEvent.LogoutClicked -> {
//                logoutUseCase()
                    uiState.updateState {
                        copy(isUserLoggedIn = false, username = "")
                    }
                }

                FacSettingsUiEvent.DeleteAccountClicked -> {
                    uiState.updateState {
                        copy(isDeleteAccountShown = true)
                    }
                }

                FacSettingsUiEvent.DeleteAccountConfirmed -> {
                    loginOperationVM.onEvent(LoginOperationEvent.DeleteAccountClick)
                    uiState.updateState {
                        copy(isDeleteAccountShown = false)
                    }
                }

                FacSettingsUiEvent.DeleteAccountDismissed -> {
                    uiState.updateState {
                        copy(isDeleteAccountShown = false)
                    }
                }

                FacSettingsUiEvent.NavigateToProfile -> {
                    val uid = firebaseUserRepository.getUserId()
                    if (uid.isNotBlank()) {
                        navigationUseCase.navigate("${QuoteAppProjectRoutes.UserProfile.route}?${ScreenKey.receiverUserId}=$uid")
                    }
                }

                FacSettingsUiEvent.AdminApproveQuestionsClicked -> {
                    // Debug-only navigation to Admin Approve screen under Question project
                    navigationUseCase.navigate(QuestionAppProjectRoutes.AdminApproveQuestion.route)
                }
            }
        }
    }
}