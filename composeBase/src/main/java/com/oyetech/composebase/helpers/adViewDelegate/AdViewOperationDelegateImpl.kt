package com.oyetech.composebase.helpers.adViewDelegate

import android.view.View
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.domain.useCases.AdsHelperUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-3.11.2024-
-12:27-
 **/

class AdViewOperationDelegateImpl : AdViewOperationDelegate {
    override val adUiState = MutableStateFlow<ImmutableList<View>>(persistentListOf())

    context(BaseViewModel)
    override fun getAdViewWithIdList(adsHelperUseCase: AdsHelperUseCase, vararg ids: String) {
        viewModelScope.launch(getDispatcherIo()) {
            adsHelperUseCase.getAdViewWithIdListWithStateFlow(*ids).collectLatest {
                val x = it.toImmutableList()
                adUiState.value = x
            }
        }
    }
}