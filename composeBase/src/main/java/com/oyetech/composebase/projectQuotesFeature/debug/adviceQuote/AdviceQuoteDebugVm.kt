package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote;

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.repository.firebase.FirebaseQuotesOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-3.02.2025-
-22:28-
 **/

class AdviceQuoteDebugVm(
    appDispatchers: AppDispatchers,
    private val firebaseQuotesOperationRepository: FirebaseQuotesOperationRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(AdviceQuoteDebugUiState())

    init {
    }

    fun onEvent(event: Any) {
        when (event) {

            else -> {}
        }
    }
}