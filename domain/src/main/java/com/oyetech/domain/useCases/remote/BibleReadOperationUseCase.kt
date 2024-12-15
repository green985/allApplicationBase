package com.oyetech.domain.useCases.remote

import com.oyetech.domain.repository.remote.BibleReadOperationRepository
import com.oyetech.models.postBody.bibles.BibleBookReadOperationRequestBody
import com.oyetech.models.postBody.bibles.BibleChapterReadOperationRequestBody
import kotlinx.coroutines.flow.Flow

/**
Created by Erdi Özbek
-23.11.2023-
-18:15-
 **/

class BibleReadOperationUseCase(private var repository: BibleReadOperationRepository) {

    private fun getRequestBodyForBookRead(id: Int): BibleBookReadOperationRequestBody {
        return BibleBookReadOperationRequestBody(id = id)
    }

    private fun getRequestBodyForChapterRead(id: Int): BibleChapterReadOperationRequestBody {
        return BibleChapterReadOperationRequestBody(id = id)
    }

    fun readBook(id: Int): Flow<Boolean> {
        var requestBody = getRequestBodyForBookRead(id)
        return repository.readBook(requestBody)
    }

    fun unreadBook(id: Int): Flow<Boolean> {
        var requestBody = getRequestBodyForBookRead(id)
        return repository.unreadBook(requestBody)
    }

    fun readChapter(id: Int): Flow<Boolean> {
        var requestBody = getRequestBodyForChapterRead(id)
        return repository.readChapter(requestBody)
    }

    fun unReadChapter(id: Int): Flow<Boolean> {
        var requestBody = getRequestBodyForChapterRead(id)
        return repository.unReadChapter(requestBody)
    }
}