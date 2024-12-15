package com.oyetech.remote.wallpaperRemote.dataSource

import com.oyetech.models.postBody.auth.BibleSaveDeviceRequestBody
import com.oyetech.models.postBody.bibles.BibleListRequestBody
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import com.oyetech.remote.wallpaperRemote.services.ApiBibleService
import com.oyetech.remote.wallpaperRemote.services.BibleService

/**
Created by Erdi Özbek
-4.11.2023-
-16:28-
 **/

class BibleDataSource(
    private var bibleService: BibleService,
    private var apiBibleService: ApiBibleService,
) {

    suspend fun getBiblesWithCountryId(languageCodeRequestBody: BibleListRequestBody) =
        bibleService.getBiblesWithCountryId(languageCodeRequestBody)

    suspend fun getBiblesBooks(bibleId: String) =
        bibleService.getBiblesBooks(bibleId)

    suspend fun getBibleChapters(bibleId: String, bookId: String) =
        bibleService.getBibleChapters(bibleId, bookId)

    suspend fun getHomePageProperty(languageCodeRequestBody: LanguageCodeRequestBody) =
        bibleService.getHomePageProperty(languageCodeRequestBody)

    suspend fun getAudioBibles() = apiBibleService.getAudioBibles()

    suspend fun saveClient(bibleSaveDeviceRequestBody: BibleSaveDeviceRequestBody) =
        bibleService.saveClient(bibleSaveDeviceRequestBody)


}