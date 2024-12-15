package com.oyetech.remote

import com.oyetech.models.postBody.broken.BasicPostBody
import com.oyetech.models.postBody.uuid.uuid.StationUuidPostBody
import com.oyetech.models.radioEntity.stationClick.StationClickResponseData
import com.oyetech.models.radioEntity.tag.TagResponseData
import com.oyetech.models.radioProject.entity.radioEntity.country.CountryResponseData
import com.oyetech.models.radioProject.entity.radioEntity.language.LanguageResponseData
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
Created by Erdi Özbek
-27.11.2022-
-23:25-
 **/

interface RadioApiService {
    @POST("languages")
    suspend fun getLanguagesList(@Body hideBoolean: BasicPostBody): Response<List<LanguageResponseData>>

    @POST("countrycodes")
    suspend fun getCountryList(@Body hideBoolean: BasicPostBody): Response<List<CountryResponseData>>

    @POST("tags")
    suspend fun getTagList(@Body hideBoolean: BasicPostBody): Response<List<TagResponseData>>

    @POST("stations/lastclick/100")
    suspend fun getLastClickStationList(@Body hideBoolean: BasicPostBody): Response<List<RadioStationResponseData>>

    @POST("stations/lastchange/100")
    suspend fun getLastChangeStationList(@Body hideBoolean: BasicPostBody): Response<List<RadioStationResponseData>>

    @POST("stations/topvote/100")
    suspend fun getTopVotedStationList(@Body hideBoolean: BasicPostBody): Response<List<RadioStationResponseData>>

    @POST("stations/topclick/100")
    suspend fun getTopClickStationList(@Body hideBoolean: BasicPostBody): Response<List<RadioStationResponseData>>

    @POST("stations/bytagexact/{tagString}")
    suspend fun getStationListWithTagParams(
        @Body hideBoolean: BasicPostBody,
        @Path("tagString") tagString: String,
    ): Response<List<RadioStationResponseData>>

    @POST("stations/bylanguageexact/{languageString}")
    suspend fun getStationListWithLanguageParams(
        @Body hideBoolean: BasicPostBody,
        @Path("languageString") tagString: String,
    ): Response<List<RadioStationResponseData>>

    @POST("stations/bycountrycodeexact/{countryString}")
    suspend fun getStationListWithCountryParams(
        @Body hideBoolean: BasicPostBody,
        @Path("countryString") tagString: String,
    ): Response<List<RadioStationResponseData>>

    @POST("stations/byuuid")
    suspend fun getStationListWithUuid(
        @Body stationUuids: StationUuidPostBody,
    ): Response<List<RadioStationResponseData>>

    @POST("stations/byname/{searchString}")
    suspend fun getStationListWithSearchParams(
        @Body hideBoolean: BasicPostBody,
        @Path("searchString") searchString: String,
    ): Response<List<RadioStationResponseData>>

    @GET("url/{stationUuid}")
    suspend fun sendStationClickEvent(@Field("stationUuid") stationUuid: String): Response<StationClickResponseData>


}