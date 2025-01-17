package com.oyetech.composebase.sharedScreens.quotes.detail;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.composebase.mappers.mapToUi.QuotesMappers
import com.oyetech.composebase.sharedScreens.quotes.uiState.QuoteUiState
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-17.01.2025-
-20:38-
 **/

class QuoteDetailVM(
    appDispatchers: AppDispatchers,
    private val quoteId: String,
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(QuoteUiState())

    init {
        getQuoteDetail(quoteId)
        viewModelScope.launch {
            quoteDataOperationRepository.setSeenQuote(quoteId).asResult().collectLatest {
                it.fold(
                    onSuccess = {
                        // do something
                    },
                    onFailure = {
                    }
                )
            }
        }
    }

    private fun getQuoteDetail(quoteId: String) {
        uiState.updateState {
            copy(isLoading = true)
        }
        viewModelScope.launch(getDispatcherIo()) {
            quoteDataOperationRepository.getSingleQuote(quoteId).asResult().collectLatest {
                it.fold(
                    onSuccess = { quote ->
                        uiState.value = QuotesMappers.mapToQuoteUiState(quote)

                    }, onFailure = {
                        uiState.updateState {
                            copy(errorMessage = ErrorHelper.getErrorMessage(it))
                        }
                    }
                )
            }
        }
    }

    fun onEvent(event: Any) {
        when (event) {

            else -> {}
        }
    }
}