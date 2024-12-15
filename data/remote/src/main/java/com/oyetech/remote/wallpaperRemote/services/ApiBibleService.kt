package com.oyetech.remote.wallpaperRemote.services

import com.oyetech.models.entity.audioBibleModels.AudioBibleListWrapperResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
Created by Erdi Özbek
-11.11.2023-
-17:54-
 **/

interface ApiBibleService {

    @Headers("api-key:4724e11d7996459d74ac0214c1e906d3")
    @GET("/v1/audio-bibles")
    suspend fun getAudioBibles(@Query("language") language: String = "eng"): Response<AudioBibleListWrapperResponseData>


}