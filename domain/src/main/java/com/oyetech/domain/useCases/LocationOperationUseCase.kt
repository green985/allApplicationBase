package com.oyetech.domain.useCases

import com.oyetech.domain.repository.helpers.LocationHelperRepository
import com.oyetech.models.postBody.location.LocationRequestBody
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-16.09.2023-
-17:09-
 **/

class LocationOperationUseCase(private var repository: LocationHelperRepository) {
    fun requestLocation() {
        repository.requestLocation()
    }

    val locationResultStateFlow: MutableStateFlow<LocationRequestBody>
        get() = repository.locationResultStateFlow

}