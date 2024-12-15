package com.oyetech.remote.wallpaperRemote.services

import com.oyetech.models.wallpaperModels.responses.WallpaperSearchResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
Created by Erdi Özbek
-7.02.2024-
-14:58-
 **/

interface WallpaperServiceApi {

    @GET("search")
    suspend fun getWallpapersWithSearchParameters(
        @QueryMap searchData: Map<String, @JvmSuppressWildcards Any>,
    ): Response<WallpaperSearchResponseData>


}