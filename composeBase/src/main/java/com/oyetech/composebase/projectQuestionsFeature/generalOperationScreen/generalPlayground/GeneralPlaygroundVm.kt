package com.oyetech.composebase.projectQuestionsFeature.generalOperationScreen.generalPlayground

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.quotesDomain.quotesData.QuotesRepository
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.repository.loginOperation.GoogleLoginRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
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
    private val navigationUseCase: NavigationUseCase,
    private val googleLoginRepository: GoogleLoginRepository,
    private val firebaseCommentOperationRepository: FirebaseCommentOperationRepository,
) : BaseViewModel(appDispatchers) {

    init {
//        fetchRandomQuotes()
//        firebaseCommentOperationRepository.getCommentsWithId("commentId")
    }

    fun initt() {
        Timber.d(" initt")
    }

    fun fetchRandomQuotes() {
        viewModelScope.launch(getDispatcherIo()) {
            quotesRepository.getQuotes().asResult().collectLatest {
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