package com.oyetech.composebase.helpers.listOperations

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * An abstract handler for managing pagination operations based on list size.
 * Unlike [PagingOperationHandler], this handler determines end-of-list by checking
 * if the returned list size is below a threshold, rather than using paging metadata.
 *
 * Usage:
 * ```kotlin
 * private val pagingHandler = object : ListSizeBasedPagingHandler<MyData>(pageThreshold = 20) {
 *     override suspend fun fetchData(pageIndex: Int): List<MyData> {
 *         return dataSource.getData(page = pageIndex)
 *     }
 * }
 *
 * fun getData(isInitial: Boolean): Flow<List<MyData>> {
 *     return pagingHandler.getDataFlow(isInitial)
 * }
 * ```
 *
 * @param T The type of individual items in the list
 * @param pageThreshold The minimum number of items expected per page. If fewer items are returned,
 *                      it indicates the end of the list has been reached.
 */
abstract class ListSizeBasedPagingHandler<T>(
    private val pageThreshold: Int,
) {

    private val _pageIndexFlow = MutableStateFlow(INITIAL_PAGE_INDEX)

    private val _endOfListFlow = MutableStateFlow(false)

    /**
     * Fetches data for the specified page index.
     * Implement this method to define how data should be fetched from your data source.
     *
     * @param pageIndex The page index to fetch
     * @return The list of items for the given page
     */
    protected abstract suspend fun fetchData(pageIndex: Int): List<T>

    /**
     * Main method to get a data flow with automatic pagination handling based on list size.
     * Handles all pagination logic internally including:
     * - State reset for initial loads
     * - End-of-list detection based on list size threshold
     * - Page advancement
     *
     * @param isInitial If true, resets pagination state before fetching; if false, continues from current page
     * @return A Flow emitting the list of items
     */
    fun getDataFlow(isInitial: Boolean): Flow<List<T>> {
        return flow {
            Timber.d(
                "getDataFlow called, isInitial: $isInitial, current pageIndex: ${getCurrentPageIndex()}, endOfList: ${isEndOfListReached()}"
            )

            resetForInitial(isInitialLoad = isInitial)

            if (isEndOfListReached()) {
                Timber.d("End of list already reached, returning empty list")
                emit(emptyList())
                return@flow
            }

            val pageIndex = getCurrentPageIndex()
            Timber.d("Fetching data for page: $pageIndex")

            val dataList = fetchData(pageIndex)

            evaluateListSize(items = dataList)

            Timber.d("Emitting ${dataList.size} items")
            emit(dataList)
        }
    }

    /**
     * Resets the paging state to initial values.
     *
     * @param isInitialLoad If true, resets the state; otherwise, does nothing.
     */
    private fun resetForInitial(isInitialLoad: Boolean) {
        if (!isInitialLoad) return

        Timber.d("Resetting paging state to initial")
        _endOfListFlow.value = false
        _pageIndexFlow.value = INITIAL_PAGE_INDEX
    }

    /**
     * Evaluates the list size and updates internal state accordingly.
     * If the list size is below the threshold, marks end-of-list as reached.
     *
     * @param isInitialLoad If true, skips the evaluation
     * @param items The list of items returned from fetchData
     */
    private fun evaluateListSize(
        items: List<T>,
    ) {
        if (hasReachedEndOfList(items)) {
            Timber.d("End of list reached (returned ${items.size} items, threshold: $pageThreshold)")
            _endOfListFlow.value = true
            return
        }

        advanceToNextPage()
    }

    /**
     * Checks if the end of the list has been reached.
     */
    fun isEndOfListReached(): Boolean = _endOfListFlow.value

    /**
     * Gets the current page index.
     */
    fun getCurrentPageIndex(): Int = _pageIndexFlow.value

    /**
     * Determines if the end of the list has been reached based on list size.
     * Returns true if the list is empty OR if the list size is below the threshold.
     */
    private fun hasReachedEndOfList(items: List<T>): Boolean {
        return items.isEmpty() || items.size < pageThreshold
    }

    /**
     * Advances to the next page.
     */
    private fun advanceToNextPage() {
        _endOfListFlow.value = false
        val nextPage = _pageIndexFlow.value + 1

        _pageIndexFlow.value = nextPage
        Timber.d("Advancing to next page: $nextPage")
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}
