package com.oyetech.domain.repository.firebase

import com.oyetech.models.radioProject.entity.firebase.RadioPlayingAnalyticsData

/**
Created by Erdi Özbek
-15.12.2024-
-01:24-
 **/

interface RadioAnalyticsOperationRepository {
    fun sendRadioPlayingAnalytics(radioPlayingData: RadioPlayingAnalyticsData)
}
