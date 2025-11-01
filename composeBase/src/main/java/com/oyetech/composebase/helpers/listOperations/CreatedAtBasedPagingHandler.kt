package com.oyetech.composebase.helpers.listOperations

import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

abstract class CreatedAtBasedPagingHandler<T>(
    private val pageThreshold: Int,
) {

    private val _lastCreatedAtCursorMs = MutableStateFlow<Long?>(null)
    private val _endOfListFlow = MutableStateFlow(false)

    protected abstract suspend fun fetchDataAfter(
        lastCreatedAtCursorMs: Long?,
        limit: Int,
    ): List<QuestionOperationResponseBody>?

    protected abstract fun extractCreatedAtEpochMs(item: T): Long

    fun getDataFlow(isInitial: Boolean): Flow<List<T>> {
        return flow {
            Timber.d(
                "getDataFlow called, isInitial: $isInitial, lastCursor: ${_lastCreatedAtCursorMs.value}, endOfList: ${isEndOfListReached()}"
            )

            resetForInitial(isInitialLoad = isInitial)

            if (isEndOfListReached()) {
                Timber.d("End of list already reached, returning empty list")
                emit(emptyList())
                return@flow
            }

            val cursor = _lastCreatedAtCursorMs.value
            Timber.d("Fetching data after createdAt(ms): $cursor with limit: $pageThreshold")

            val dataList = fetchDataAfter(cursor, pageThreshold)

            if (dataList == null) {
                Timber.d("Fetched data is null, emitting empty list")
                error("Fetched data is null")
                return@flow
            }

            if (dataList.isNotEmpty()) {
                val newCursor = extractCreatedAtEpochMs(dataList.last() as T)
                _lastCreatedAtCursorMs.value = newCursor
                Timber.d("Updated lastCreatedAt cursor(ms) to: $newCursor")
            }

            evaluateListSize(items = dataList as List<T>)

            Timber.d("Emitting ${dataList.size} items")
            emit(dataList)
        }
    }

    fun isEndOfListReached(): Boolean = _endOfListFlow.value

    fun getLastCreatedAtCursorMs(): Long? = _lastCreatedAtCursorMs.value

    fun resetAll() {
        Timber.d("Resetting paging state (manual)")
        _endOfListFlow.value = false
        _lastCreatedAtCursorMs.value = null
    }

    private fun resetForInitial(isInitialLoad: Boolean) {
        if (!isInitialLoad) return
        Timber.d("Resetting paging state to initial")
        _endOfListFlow.value = false
        _lastCreatedAtCursorMs.value = null
    }

    private fun evaluateListSize(items: List<T>) {
        if (hasReachedEndOfList(items)) {
            Timber.d("End of list reached (returned ${items.size} items, threshold: $pageThreshold)")
            _endOfListFlow.value = true
            return
        }
        _endOfListFlow.value = false
    }

    private fun hasReachedEndOfList(items: List<T>): Boolean {
        return items.isEmpty() || items.size < pageThreshold
    }
}
