package com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen;

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-25.01.2025-
-18:19-
 **/

class QuoteSettingsVm(appDispatchers: AppDispatchers) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(QuoteSettingsUiState())
    val toolbarState = MutableStateFlow(QuoteToolbarState(LanguageKey.settings))

    init {
    }

    fun onEvent(event: Any) {
        when (event) {

            else -> {}
        }
    }
}