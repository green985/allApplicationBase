package com.oyetech.composebase.projectRadioFeature.screens.generalOperationScreen.generalPlayground

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.core.coroutineHelper.AppDispatchers
import com.oyetech.core.coroutineHelper.asResult
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-16.12.2024-
-22:55-
 **/

class GeneralPlaygroundVm(
    appDispatchers: AppDispatchers,
    private val quotesRepository: QuotesRepository,
) : BaseViewModel(appDispatchers) {

    init {
        fetchRandomQuotes()
    }

    fun initt() {
        Timber.d(" initt")
    }

    fun fetchRandomQuotes() {
        viewModelScope.launch(getDispatcherIo()) {
            quotesRepository.fetchQuotes().asResult().collectLatest {
                it.fold(
                    onSuccess = {
                        it.toString()
                        // handle success
                    },
                    onFailure = {
                        // handle error
                        it.printStackTrace()
                    }
                )
            }
        }
    }

}