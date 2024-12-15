package com.oyetech.helper.recyclerView

import androidx.recyclerview.widget.RecyclerView
import com.oyetech.extension.getFirstVisibleItemPosition

const val ADAPTER_ITEM_THRESHOLD = 5

abstract class LastItemListener(
    private var visibleThreshold: Int = ADAPTER_ITEM_THRESHOLD,
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        // init
        val layoutManager = recyclerView.layoutManager
        val adapter = recyclerView.adapter
        var itemCountt = adapter?.itemCount ?: 0
        if (layoutManager!!.childCount > 0) {
            try {

                var firstVisibleItem = recyclerView.getFirstVisibleItemPosition()

                var thresholds = itemCountt - 30
                if (thresholds > 0 && firstVisibleItem > thresholds) {
                    onLastItemVisible()
                    return
                }

                // Calculations..
                val indexOfLastItemViewVisible = layoutManager.childCount - visibleThreshold
                val lastItemViewVisible = layoutManager.getChildAt(indexOfLastItemViewVisible)
                val adapterPosition = layoutManager.getPosition(lastItemViewVisible!!)
                val isLastItemVisible = adapterPosition == adapter!!.itemCount - visibleThreshold

                // check
                if (isLastItemVisible) onLastItemVisible() // callback
            } catch (e: Exception) {
                // e.printStackTrace()
            }
        }
    }

    /**
     * Here you should load more items because user is seeing the last item of the list.
     * Advice: you should add a bollean value to the class
     * so that the method [.onLastItemVisible] will be triggered only once
     * and not every time the user touch the screen ;)
     */
    abstract fun onLastItemVisible()
}
