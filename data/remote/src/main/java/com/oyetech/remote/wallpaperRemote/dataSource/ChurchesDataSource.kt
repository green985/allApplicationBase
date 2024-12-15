package com.oyetech.remote.wallpaperRemote.dataSource

import com.oyetech.cripto.privateKeys.PrivateKeys
import com.oyetech.models.entity.location.hereApiLocation.HereLocationDataResponse
import com.oyetech.models.postBody.location.LocationHereRequestBody
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.const.HelperConstant.hereRequestUrl
import com.oyetech.remote.wallpaperRemote.services.HideApiService
import retrofit2.Response

/**
Created by Erdi Özbek
-18.09.2023-
-15:28-
 **/

class ChurchesDataSource(private var service: HideApiService) {

    suspend fun getChurchesWithUrl(hereRequestBody: LocationHereRequestBody): Response<HereLocationDataResponse> {
        return service.getChurchesWithUrl(
            url = hereRequestUrl,
            at = hereRequestBody.locationString,
            q = "church",
            apiKey = PrivateKeys.HERE_LOCATION_API_KEY,
            size = HelperConstant.HERE_PLACE_SIZE
        )

    }
}