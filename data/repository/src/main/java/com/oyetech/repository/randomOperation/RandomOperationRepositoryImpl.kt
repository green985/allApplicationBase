package com.oyetech.repository.randomOperation

import com.oyetech.domain.repository.randomOperation.RandomOperationRepository
import com.oyetech.models.randomOperationModels.MoonPhaseResponse
import com.oyetech.remote.randomOperationRemote.RandomOperationDataSource
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-8.07.2025-
-00:45-
 **/

class RandomOperationRepositoryImpl(private val randomOperationDataSource: RandomOperationDataSource) :
    RandomOperationRepository {

    override fun getMoonPhase(unixTime: Long): Flow<List<MoonPhaseResponse>> {
        return randomOperationDataSource.getMoonPhase(unixTime)
    }

}