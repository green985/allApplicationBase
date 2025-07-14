package com.oyetech.composebase.experimental.moonOperation;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.moonOperation.MoonOperationEvent.Moon
import com.oyetech.domain.repository.randomOperation.RandomOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
            val unix = System.currentTimeMillis() / 1000
            val result = randomOperationRepository.getMoonPhase(unix).asResult().collectLatest {
                it.fold(
                    onSuccess = { dtoList ->
                        val dto = dtoList.firstOrNull()
                        if (dto != null) {
                            uiState.updateState {
                                copy(
                                    phaseName = dto.phase,
//                                fraction = dto.fraction,
                                    error = null
                                )
                            }
                        } else {
                            uiState.updateState { copy(error = "No moon data") }
                        }
                    },
                    onFailure = { ex ->
                        uiState.updateState { copy(error = ex.localizedMessage ?: "Unknown error") }
                    }
                )
            }
        }
    }
}