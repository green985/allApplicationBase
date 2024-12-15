package com.oyetech.models.radioProject.entity.firebase

/**
Created by Erdi Özbek
-15.12.2024-
-01:26-
 **/

data class RadioPlayingAnalyticsData(
    val radioStationUuid: String = "",
    val radioName: String,
    val playingTimeMilis: Long = 0,
)