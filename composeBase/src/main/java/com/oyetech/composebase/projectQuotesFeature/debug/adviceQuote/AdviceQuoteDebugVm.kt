package com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote;

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote.AdviceQuoteDebugEvent.ApproveQuote
import com.oyetech.domain.repository.firebase.FirebaseQuotesDebugOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-3.02.2025-
-22:28-
 **/

class AdviceQuoteDebugVm(
    appDispatchers: AppDispatchers,
    private val firebaseQuotesDebugOperationRepository: FirebaseQuotesDebugOperationRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(AdviceQuoteDebugUiState())

    val adviceQuotesPage =
        Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                AdviceQuoteDebugPagingSource(
                    firebaseQuotesDebugOperationRepository,
                    uiState
                )
            }
        ).flow.cachedIn(viewModelScope)

    init {
//        getAdviceQuote()
    }

    fun onEvent(event: AdviceQuoteDebugEvent) {
        when (event) {
            is AdviceQuoteDebugEvent.ApproveQuote -> {
                approveQuoteEvent(event)
            }

            else -> {}
        }
    }

    private fun approveQuoteEvent(event: ApproveQuote) {
        viewModelScope.launch(getDispatcherIo()) {
            val documentId = event.id
            firebaseQuotesDebugOperationRepository.approveAdviceQuote(documentId).asResult()
                .collectLatest {
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
}