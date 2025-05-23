package com.oyetech.composebase.projectQuotesFeature.quotes.detail;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.helpers.errorHelper.ErrorHelper
import com.oyetech.composebase.mappers.mapToUi.QuotesMappers
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationVm
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailEvent.ClickNextButton
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailEvent.ClickPreviousButton
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailEvent.LongClickForCopy
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent.BackButtonClick
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent.OnActionButtonClick
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.contextHelper.copyToClipboard
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-17.01.2025-
-20:38-
 **/

class QuoteDetailVm(
    appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    var quoteId: String = "",
    private val quoteDataOperationRepository: QuoteDataOperationRepository,
    private val snackbarDelegate: SnackbarDelegate,
    val contentOperationVm: ContentOperationVm,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(QuoteUiState(quoteId = quoteId))

    val toolbarState = MutableStateFlow(
        QuoteToolbarState(
            title = "Quote Detail",
            showBackButton = true
        )
    )

    init {
        contentOperationVm.initContentOperationState(listOf(quoteId), inList = false)

        getQuoteDetail(quoteId)
        viewModelScope.launch(getDispatcherIo()) {
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

    private fun getQuoteDetail(quoteId: String = "", isRandom: Boolean = false) {
        uiState.updateState {
            copy(isLoading = true)
        }
        viewModelScope.launch(getDispatcherIo()) {
            quoteDataOperationRepository.getSingleQuote(
                if (isRandom) {
                    "randomSingle"
                } else quoteId
            ).asResult().collectLatest {
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

    fun onEvent(event: QuoteDetailEvent) {
        when (event) {

            ClickNextButton -> {
                getQuoteDetail(isRandom = true)
            }

            ClickPreviousButton -> {
                getQuoteDetail(isRandom = true)
            }

            LongClickForCopy -> {
                copyToClipboardQuoteText()
            }

            else -> {}
        }
    }

    private fun copyToClipboardQuoteText() {
        val quoteText = uiState.value.text
        context.copyToClipboard(quoteText)
        snackbarDelegate.triggerSnackbarState(LanguageKey.copyToClipboardSuccess)
    }

    fun onToolbarEvent(it: QuoteToolbarEvent) {
        when (it) {
            is BackButtonClick -> {

            }

            is OnActionButtonClick -> TODO()
        }
    }

}