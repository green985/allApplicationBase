package com.oyetech.composebase.helpers.adViewDelegate

import android.view.View
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.useCases.AdsHelperUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-3.11.2024-
-12:26-
 **/

interface AdViewOperationDelegate {
    val adUiState: MutableStateFlow<ImmutableList<View>>
//    fun getAdViewWithIdList(adsHelperUseCase : AdsHelperUseCase,vararg ids: String)

    context(BaseViewModel)
    fun getAdViewWithIdList(
        adsHelperUseCase: AdsHelperUseCase,
        vararg ids: String,
    )
}