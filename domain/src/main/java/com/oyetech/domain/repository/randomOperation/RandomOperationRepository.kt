package com.oyetech.domain.repository.randomOperation

/**
Created by Erdi Özbek
-8.07.2025-
-00:42-
 **/

interface RandomOperationRepository {

    fun getMoonPhase(unixTime: Long): kotlinx.coroutines.flow.Flow<List<com.oyetech.models.randomOperationModels.MoonPhaseResponse>>

}
