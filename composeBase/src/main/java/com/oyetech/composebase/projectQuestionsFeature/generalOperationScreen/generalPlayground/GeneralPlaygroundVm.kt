package com.oyetech.composebase.projectQuestionsFeature.generalOperationScreen.generalPlayground

import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import timber.log.Timber

/**
Created by Erdi Özbek
-16.12.2024-
-22:55-
 **/

class GeneralPlaygroundVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val firebaseCommentOperationRepository: FirebaseCommentOperationRepository,
) : BaseViewModel(appDispatchers) {

    fun initt() {
        Timber.d(" initt")
    }
}
