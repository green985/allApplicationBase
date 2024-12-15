package com.oyetech.extension

import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.oyetech.core.ext.doInTryCatch
import com.oyetech.models.utils.const.HelperConstant
import timber.log.Timber

/**
Created by Erdi Özbek
-16.03.2022-
-15:19-
 **/

fun RecyclerView.initRecyclerViewProperty(
    adapter: RecyclerView.Adapter<*>,
    isReverseLayout: Boolean = false,
    layoutManager: LayoutManager? = null,
) {
    if (layoutManager != null) {
        this.layoutManager = layoutManager
    } else {
        this.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.VERTICAL, isReverseLayout)
    }
    this.itemAnimator = null
    this.recycledViewPool.setMaxRecycledViews(0, 10)
    this.setItemViewCacheSize(10)
    this.adapter = adapter
}

fun RecyclerView.smoothSnapToPosition(
    position: Int,
    snapMode: Int = LinearSmoothScroller.SNAP_TO_END,
) {
    doInTryCatch {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return HelperConstant.SMOOTH_SCROLL_SPEED
            }
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }
}

fun RecyclerView.smoothSnapToPositionWithLayoutManager(
    position: Int,
) {
    doInTryCatch {
        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
    }
}

fun RecyclerView.getFirstVisibleItemPosition(): Int {
    val recyclerView = this
    if (recyclerView.layoutManager == null) return 0
    var layoutManager = recyclerView.layoutManager as LinearLayoutManager
    return layoutManager.findFirstVisibleItemPosition()
}

fun RecyclerView.getLastVisibleItemPosition(): Int {
    val recyclerView = this
    if (recyclerView.layoutManager == null) return 0
    var layoutManager = recyclerView.layoutManager as LinearLayoutManager
    return layoutManager.findLastVisibleItemPosition()
}

fun RecyclerView.getLastCompletelyVisibleItemPosition(): Int {
    val recyclerView = this
    if (recyclerView.layoutManager == null) return 0
    var layoutManager = recyclerView.layoutManager as LinearLayoutManager
    return layoutManager.findLastCompletelyVisibleItemPosition()
}

fun RecyclerView.Adapter<*>?.setAdapterDataObserverForItemChange() {
    this?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            Timber.d("onChangeddd onItemRangeRemoved")
        }

        override fun onChanged() {
            super.onChanged()
            Timber.d("onChangeddd ==")
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            Timber.d("Adapter onChanged")
            Timber.d("onChangeddd onItemRangeChanged" + itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            Timber.d("onChangeddd onItemRangeInserted " + itemCount)
        }
    })
}
