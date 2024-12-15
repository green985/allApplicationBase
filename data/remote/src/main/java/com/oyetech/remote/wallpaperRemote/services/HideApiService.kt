package com.oyetech.remote.wallpaperRemote.services

import com.oyetech.models.entity.location.hereApiLocation.HereLocationDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
Created by Erdi Özbek
-19.09.2023-
-13:55-
 **/

interface HideApiService {

    @GET
    suspend fun getChurchesWithUrl(
        @Url url: String,
        @Query("at") at: String,
        @Query("q") q: String,
        @Query("apiKey") apiKey: String,
        @Query("size") size: Int,
    ): Response<HereLocationDataResponse>

}