package com.oyetech.composebase.experimental.moonOperation;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.experimental.moonOperation.MoonOperationEvent.Moon
import com.oyetech.domain.repository.randomOperation.RandomOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-8.07.2025-
-00:14-
 **/

class MoonOperationVm(
    appDispatchers: AppDispatchers,
    private val randomOperationRepository: RandomOperationRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(MoonOperationUiState("moon"))

    init {
    }

    override fun onEvent(event: Any) {
        if (event is MoonOperationEvent) {
            when (event) {
                Moon -> TODO()
            }
        }
    }

    fun getMoonOperationData() {
        viewModelScope.launch {
            val unixTime = System.currentTimeMillis() / 1000
            val response =
                randomOperationRepository.getMoonPhase(unixTime).asResult().collectLatest {
                    it.fold(
                        onSuccess = { moonPhase ->
                            Timber.d("Fetched moon phase: $moonPhase")
//                        uiState.updateState { currentState ->
//                            currentState.copy(
//                                moonPhase = moonPhase,
//                                error = null
//                            )
//                        }
                        },
                        onFailure = { error ->
//                        Timber.e(error, "Error fetching moon phase")
//                        uiState.updateState { currentState ->
//                            currentState.copy(
//                                error = error.message ?: "Unknown error"
//                            )
//                        }
                        }
                    )
                }
            Timber.d("mooooooonnn======= " + response.toString())
        }
    }
}