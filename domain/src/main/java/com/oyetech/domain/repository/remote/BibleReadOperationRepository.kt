package com.oyetech.domain.repository.remote

import com.oyetech.models.postBody.bibles.BibleBookReadOperationRequestBody
import com.oyetech.models.postBody.bibles.BibleChapterReadOperationRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-23.11.2023-
-18:14-
 **/

interface BibleReadOperationRepository {
    fun readBook(bibleBookReadOperationRequestBody: BibleBookReadOperationRequestBody): Flow<Boolean>
    fun unreadBook(bibleBookReadOperationRequestBody: BibleBookReadOperationRequestBody): Flow<Boolean>
    fun readChapter(bibleChapterDetailResponseData: BibleChapterReadOperationRequestBody): Flow<Boolean>
    fun unReadChapter(bibleChapterDetailResponseData: BibleChapterReadOperationRequestBody): Flow<Boolean>
}