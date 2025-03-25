package com.oyetech.composebase.sharedScreens.settings

import com.oyetech.composebase.BuildConfig
import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIState
import com.oyetech.languageModule.keyset.LanguageKey

data class FacSettingsUiState(
    val contactWithMeText: String = LanguageKey.contactWithUs,
    val infoText: String = LanguageKey.applicationInfoText,
    val privacyPolicyText: String = LanguageKey.privacyPolicyText,
    val termsAndConditionText: String = LanguageKey.termsAndConditionText,
    val deleteAccountInfoText: String = LanguageKey.deleteAccountButtonText,
    val rateUs: String = LanguageKey.rateUs,

    val isUserLoggedIn: Boolean = false,
    val username: String = "",
    val isDeleteAccountShown: Boolean = false,
    val isDebug: Boolean = BuildConfig.DEBUG,
) : BaseUIState() {
}

sealed class FacSettingsUiEvent : BaseEvent() {
    data object ContactClicked : FacSettingsUiEvent()
    data object InfoClicked : FacSettingsUiEvent()
    data object PrivacyPolicyClicked : FacSettingsUiEvent()
    data object TermsAndConditionsClicked : FacSettingsUiEvent()
    data object LogoutClicked : FacSettingsUiEvent()
    data object DeleteAccountClicked : FacSettingsUiEvent()
    data object DeleteAccountConfirmed : FacSettingsUiEvent()
    data object DeleteAccountDismissed : FacSettingsUiEvent()
}