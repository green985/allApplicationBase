package com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen

import com.oyetech.composebase.base.BaseEvent

/**
Created by Erdi Özbek
-25.01.2025-
-18:18-
 **/

data class QuoteSettingsUiState(
    val isLoading: Boolean = false,
    val isDeleteDialogShown: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val username: String = "",
)

sealed class QuoteSettingsEvent : BaseEvent() {
    object OnQuoteAdviceScreenClicked : QuoteSettingsEvent()
    object DeleteAccountConfirm : QuoteSettingsEvent()
    object DeleteAccountClick : QuoteSettingsEvent()
    object DismissDialog : QuoteSettingsEvent()
    object OnContactWithMeClicked : QuoteSettingsEvent()
//    data class Idle(val data: Int) : QuoteSettingsEvent()
//    object Idlee : QuoteSettingsEvent()
}