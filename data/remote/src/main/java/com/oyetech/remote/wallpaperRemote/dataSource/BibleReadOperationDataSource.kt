package com.oyetech.remote.wallpaperRemote.dataSource

import com.oyetech.models.postBody.bibles.BibleBookReadOperationRequestBody
import com.oyetech.models.postBody.bibles.BibleChapterReadOperationRequestBody
import com.oyetech.remote.wallpaperRemote.services.BibleService

/**
Created by Erdi Özbek
-23.11.2023-
-18:08-
 **/

class BibleReadOperationDataSource(
    private var bibleService: BibleService,
) {

    suspend fun readBook(bibleBookReadOperationRequestBody: BibleBookReadOperationRequestBody) =
        bibleService.readBook(bibleBookReadOperationRequestBody)

    suspend fun unreadBook(bibleBookReadOperationRequestBody: BibleBookReadOperationRequestBody) =
        bibleService.unreadBook(bibleBookReadOperationRequestBody)

    suspend fun readChapter(bibleChapterReadOperationRequestBody: BibleChapterReadOperationRequestBody) =
        bibleService.readChapter(bibleChapterReadOperationRequestBody)

    suspend fun unReadChapter(bibleChapterReadOperationRequestBody: BibleChapterReadOperationRequestBody) =
        bibleService.unReadChapter(bibleChapterReadOperationRequestBody)

}