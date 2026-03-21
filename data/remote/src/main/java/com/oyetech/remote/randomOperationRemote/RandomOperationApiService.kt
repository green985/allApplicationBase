package com.oyetech.remote.randomOperationRemote

import com.oyetech.models.randomOperationModels.MoonPhaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
Created by Erdi Özbek
-8.07.2025-
-00:32-
 **/

interface RandomOperationApiService {

    @GET
    suspend fun getMoonPhase(
        @Url url: String = "https://api.farmsense.net/v1/moonphases/",
        @Query("d") timestamp: Long,
    ): Response<List<MoonPhaseResponse>>
}
