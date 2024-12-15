package com.oyetech.domain.useCases.remote

import com.oyetech.domain.repository.remote.BibleOperationRepository
import com.oyetech.models.entity.audioBibleModels.AudioBiblePropertyResponseData
import com.oyetech.models.entity.bibleModels.BiblePropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleBookPropertyResponseData
import com.oyetech.models.entity.bibleProperties.BibleChapterDetailResponseData
import com.oyetech.models.entity.bibleProperties.BibleChapterPropertyResponseData
import com.oyetech.models.entity.homePage.HomePagePropertyResponseData
import com.oyetech.models.postBody.auth.BibleSaveDeviceRequestBody
import com.oyetech.models.postBody.bibles.BibleListRequestBody
import com.oyetech.models.postBody.world.LanguageCodeRequestBody
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

/**
Created by Erdi Özbek
-4.11.2023-
-16:31-
 **/

class BibleOperationUseCase(private var bibleOperationRepository: BibleOperationRepository) {

    var bibleBookListStateFlow =
        MutableSharedFlow<List<BibleBookPropertyResponseData>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    var saveClientPropertyTrigger =
        MutableSharedFlow<Boolean>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    var bibleChapterListStateFlow = MutableSharedFlow<List<BibleChapterPropertyResponseData>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun getBiblesWithCountryId(bibleListRequestBody: BibleListRequestBody): Flow<List<BiblePropertyResponseData>> {
        return bibleOperationRepository.getBiblesWithCountryId(bibleListRequestBody)
    }

    fun getBiblesBooks(bibleId: String): Flow<List<BibleBookPropertyResponseData>> {
        return bibleOperationRepository.getBiblesBooks(bibleId).map {
            if (it.isNotEmpty()) {
                bibleBookListStateFlow.tryEmit(it)
            }
            it
        }
    }

    fun getBibleChapters(bibleId: String, bookId: String): Flow<BibleChapterDetailResponseData> {
        return bibleOperationRepository.getBibleChapters(bibleId, bookId)
    }

    fun getHomePageProperty(languageCodeRequestBody: LanguageCodeRequestBody): Flow<HomePagePropertyResponseData> {
        return bibleOperationRepository.getHomePageProperty(languageCodeRequestBody)
    }

    fun saveClient(bibleSaveDeviceRequestBody: BibleSaveDeviceRequestBody): Flow<Boolean> {
        return bibleOperationRepository.saveClient(bibleSaveDeviceRequestBody)
    }

    suspend fun getAudioBibles(): Flow<List<AudioBiblePropertyResponseData>> {
        return bibleOperationRepository.getAudioBibles()
    }

    fun getLastBibleBookList(): List<BibleBookPropertyResponseData> {
        return bibleBookListStateFlow.replayCache.first()
    }

    fun getLastBibleChapterList(): List<BibleChapterPropertyResponseData> {
        return bibleChapterListStateFlow.replayCache.first()
    }

    fun getBibleChapterListFlow(): SharedFlow<List<BibleChapterPropertyResponseData>> {
        return bibleChapterListStateFlow.asSharedFlow()
    }

    fun getSaveClientTriggerFlow(): SharedFlow<Boolean> {
        return saveClientPropertyTrigger.asSharedFlow()
    }

    fun getBibleBookWithBookId(bookId: String): BibleBookPropertyResponseData? {
        var bookList = getLastBibleBookList()
        var foundedBookPropertyResponseData = bookList.find {
            it.bookId.toString() == bookId
        }

        return foundedBookPropertyResponseData
    }

    fun setMarkChapterId(chapterId: Int, isRead: Boolean) {
        var chapterList = getLastBibleChapterList()
        var newChapterList = chapterList.map {
            if (it.chapterId == chapterId) {
                it.isRead = isRead
            }
            it
        }
        bibleChapterListStateFlow.tryEmit(newChapterList.toMutableList())
    }

    fun setMarkChapterAllIsRead(isRead: Boolean) {
        var chapterList = getLastBibleChapterList()
        var newChapterList = arrayListOf<BibleChapterPropertyResponseData>()
        newChapterList.addAll(chapterList)
        newChapterList.map {
            it.isRead = isRead
            it
        }
        bibleChapterListStateFlow.tryEmit(newChapterList.toMutableList())
    }

    fun setMarkBookId(bookId: Int, isRead: Boolean) {
        var bookList = getLastBibleBookList()
        var newBookList = arrayListOf<BibleBookPropertyResponseData>()
        newBookList.addAll(bookList)
        newBookList.map {
            if (it.bookId == bookId) {
                it.isRead = isRead
            }
            it
        }
        setMarkChapterAllIsRead(isRead)
        bibleBookListStateFlow.tryEmit(newBookList.toMutableList())

    }

}