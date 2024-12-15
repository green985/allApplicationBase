package com.oyetech.domain.repository.helpers

import com.oyetech.models.postBody.location.LocationRequestBody
import kotlinx.coroutines.flow.MutableStateFlow

interface LocationHelperRepository {

    fun requestLocation()

    val locationResultStateFlow: MutableStateFlow<LocationRequestBody>

}