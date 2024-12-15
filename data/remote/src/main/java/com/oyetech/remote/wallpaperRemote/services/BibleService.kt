package com.oyetech.remote.wallpaperRemote.services

import com.oyetech.models.entity.GenericResponse
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleBookPropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleChapterDetailResponseData
import com.oyetech.models.entity.homePage.HomePagePropertyResponseData
import com.oyetech.models.entity.language.country.CountryListDataResponse
import com.oyetech.models.entity.language.world.LanguagesListDataResponse
import com.oyetech.models.postBody.auth.BibleSaveDeviceRequestBody
import com.oyetech.models.postBody.bibles.BibleBookReadOperationRequestBody
import com.oyetech.models.postBody.bibles.BibleChapterReadOperationRequestBody
import com.oyetech.models.postBody.bibles.BibleListRequestBody
import com.oyetech.models.postBody.feedback.FeedbackOperationRequestBody
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
Created by Erdi Özbek
-18.09.2023-
-15:27-
 **/

interface BibleService {

    @GET
    suspend fun getChurchesWithUrl(
        @Url url: String,
        @Query("at") at: String,
        @Query("q") q: String,
        @Query("apiKey") apiKey: String,
    ): Response<Boolean>

    @POST("/bible/languages")
    suspend fun getLanguages(
        @Body languageCodeRequestBody: LanguageCodeRequestBody,
    ): Response<GenericResponse<LanguagesListDataResponse>>

    @POST("/bible/countries")
    suspend fun getCountries(
        @Body languageCodeRequestBody: LanguageCodeRequestBody,
    ): Response<GenericResponse<CountryListDataResponse>>

    @POST("/bible/bibles")
    suspend fun getBiblesWithCountryId(
        @Body languageCodeRequestBody: BibleListRequestBody,
    ): Response<GenericResponse<List<BiblePropertyResponseData>>>

    @GET("/bible/books")
    suspend fun getBiblesBooks(
        @Query("bibleId") bibleId: String,
    ): Response<GenericResponse<List<BibleBookPropertyResponseData>>>

    @GET("/bible/chapters")
    suspend fun getBibleChapters(
        @Query("bibleId") bibleId: String,
        @Query("bookId") bookId: String,
    ): Response<GenericResponse<BibleChapterDetailResponseData>>

    @POST("/bible/home")
    suspend fun getHomePageProperty(
        @Body languageCodeRequestBody: LanguageCodeRequestBody,
    ): Response<GenericResponse<HomePagePropertyResponseData>>

    @POST("/bible/saveclient")
    suspend fun saveClient(
        @Body saveClientPostBody: BibleSaveDeviceRequestBody,
    ): Response<GenericResponse<Boolean>>

    @POST("/bible/readbook")
    suspend fun readBook(
        @Body bibleBookPropertyResponseData: BibleBookReadOperationRequestBody,
    ): Response<GenericResponse<Boolean>>

    @POST("/bible/unreadbook")
    suspend fun unreadBook(
        @Body bibleBookPropertyResponseData: BibleBookReadOperationRequestBody,
    ): Response<GenericResponse<Boolean>>

    @POST("/bible/readchapter")
    suspend fun readChapter(
        @Body bibleChapterDetailResponseData: BibleChapterReadOperationRequestBody,
    ): Response<GenericResponse<Boolean>>

    @POST("/bible/unreadchapter")
    suspend fun unReadChapter(
        @Body bibleChapterDetailResponseData: BibleChapterReadOperationRequestBody,
    ): Response<GenericResponse<Boolean>>

    @POST("/bible/feedback")
    suspend fun sendFeedback(
        @Body feedbackOperationRequestBody: FeedbackOperationRequestBody,
    ): Response<GenericResponse<Boolean>>

    /*
    POST /bible/readbook
{
  "bookId": id of book
}
POST /bible/readchapter
{
  "bookId": id of chapter
}
POST /bible/unreadbook
{
  "bookId": id of book
}
POST /bible/unreadchapter
{
  "bookId": id of chapter
}
     */

    //GET /bible/home

    // /bible/chapters?bibleId=139&bookId=8647

}