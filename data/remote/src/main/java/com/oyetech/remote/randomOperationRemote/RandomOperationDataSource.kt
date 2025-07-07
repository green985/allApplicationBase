package com.oyetech.remote.randomOperationRemote

import com.oyetech.models.randomOperationModels.MoonPhaseResponse
import com.oyetech.remote.helper.interceptTrueForm
import kotlinx.coroutines.flow.Flow

class RandomOperationDataSource(private var randomOperationApiService: RandomOperationApiService) {

    fun getMoonPhase(timestamp: Long): Flow<List<MoonPhaseResponse>> {
        return interceptTrueForm {
            randomOperationApiService.getMoonPhase(timestamp = timestamp)
        }
    }
}
