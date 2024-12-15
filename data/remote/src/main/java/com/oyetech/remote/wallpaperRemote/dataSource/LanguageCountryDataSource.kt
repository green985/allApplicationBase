package com.oyetech.remote.wallpaperRemote.dataSource

import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import com.oyetech.remote.wallpaperRemote.services.BibleService

/**
Created by Erdi Özbek
-30.10.2023-
-23:04-
 **/

class LanguageCountryDataSource(private var bibleService: BibleService) {
    suspend fun getLanguages(languageCodeRequestBody: LanguageCodeRequestBody) =
        bibleService.getLanguages(languageCodeRequestBody)

    suspend fun getCountries(languageCodeRequestBody: LanguageCodeRequestBody) =
        bibleService.getCountries(languageCodeRequestBody)

}