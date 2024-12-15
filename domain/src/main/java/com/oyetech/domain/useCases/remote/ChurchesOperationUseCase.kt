package com.oyetech.domain.useCases.remote

import com.oyetech.cripto.privateKeys.PrivateKeys
import com.oyetech.domain.repository.remote.ChurchesRemoteRepository
import com.oyetech.models.entity.location.hereApiLocation.HereLocationDataResponse
import com.oyetech.models.postBody.location.LocationRequestBody
import com.oyetech.models.postBody.location.createLocationHereRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-18.09.2023-
-22:00-
 **/

class ChurchesOperationUseCase(private var repository: ChurchesRemoteRepository) {

    suspend fun getChurches(locationRequestBody: LocationRequestBody): Flow<HereLocationDataResponse> {

        var requestBody = createLocationHereRequestBody(
            apiKey = PrivateKeys.HERE_LOCATION_API_KEY,
            locationRequestBody
        )
        return repository.getChurches(requestBody)
    }


}