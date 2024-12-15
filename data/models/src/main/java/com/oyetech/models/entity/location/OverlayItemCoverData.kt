package com.oyetech.models.entity.location

import androidx.annotation.Keep
import com.oyetech.models.entity.location.hereApiLocation.LocationItem

/**
Created by Erdi Özbek
-20.09.2023-
-22:50-
 **/
@Keep
data class OverlayItemCoverData(
    var overlayItem: Any,
    var id: String,

    var locationItem: LocationItem? = null,

    )
