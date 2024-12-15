package com.oyetech.domain.repository.remote

import com.oyetech.models.entity.audioBibleModels.AudioBiblePropertyResponseData
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleBookPropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleChapterDetailResponseData
import com.oyetech.models.entity.homePage.HomePagePropertyResponseData
import com.oyetech.models.postBody.auth.BibleSaveDeviceRequestBody
import com.oyetech.models.postBody.bibles.BibleListRequestBody
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import kotlinx.coroutines.flow.Flow

interface BibleOperationRepository {

    fun getBiblesWithCountryId(bibleListRequestBody: BibleListRequestBody): Flow<List<BiblePropertyResponseData>>
    suspend fun getAudioBibles(): Flow<List<AudioBiblePropertyResponseData>>
    fun getBiblesBooks(bookId: String): Flow<List<BibleBookPropertyResponseData>>
    fun getBibleChapters(bibleId: String, bookId: String): Flow<BibleChapterDetailResponseData>
    fun saveClient(bibleSaveDeviceRequestBody: BibleSaveDeviceRequestBody): Flow<Boolean>
    fun getHomePageProperty(languageCodeRequestBody: LanguageCodeRequestBody): Flow<HomePagePropertyResponseData>
}
