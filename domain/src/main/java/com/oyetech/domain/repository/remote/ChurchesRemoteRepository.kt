package com.oyetech.domain.repository.remote

import com.oyetech.models.entity.location.hereApiLocation.HereLocationDataResponse
import com.oyetech.models.postBody.location.LocationHereRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-18.09.2023-
-21:57-
 **/

interface ChurchesRemoteRepository {

    suspend fun getChurches(requestBody: LocationHereRequestBody): Flow<HereLocationDataResponse>
}