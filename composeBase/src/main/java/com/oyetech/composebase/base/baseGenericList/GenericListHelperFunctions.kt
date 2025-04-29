package com.oyetech.composebase.base.baseGenericList

/**
Created by Erdi Özbek
-22.03.2025-
-19:46-
 **/

import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

fun CoroutineScope.safeScrollToItem(
    lazyListState: LazyListState,
    index: Int,
    scrollOffset: Int = 0,
    animated: Boolean = true,
) {
    this.launch {
        try {
            val totalItems = lazyListState.layoutInfo.totalItemsCount

            if (totalItems == 0) {
                Timber.w("safeScrollToItem: LazyList has 0 items. Scroll skipped.")
                return@launch
            }

            if (index < 0 || index >= totalItems) {
                Timber.w("safeScrollToItem: Invalid index $index for list of $totalItems items.")
                return@launch
            }

            if (animated) {
                lazyListState.animateScrollToItem(index, scrollOffset)
            } else {
                lazyListState.scrollToItem(index, scrollOffset)
            }

            Timber.d("safeScrollToItem: Successfully scrolled to item $index")

        } catch (e: Exception) {
            Timber.e(e, "safeScrollToItem: Failed to scroll to item $index")
        }
    }
}
