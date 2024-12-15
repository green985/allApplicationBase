package com.oyetech.core.distanceHelper

import com.oyetech.core.ext.doInTryCatch

/**
Created by Erdi Özbek
-21.01.2024-
-17:18-
 **/

object DistanceOperationHelper {

    fun calculateDistance(distanceInt: Int?): String {
        if (distanceInt == null) return "1"
        doInTryCatch {
            var distanceKm = distanceInt / 100
            if (distanceKm == 0) return "1"

            return distanceKm.toString()
        }

        return "1"
    }
}