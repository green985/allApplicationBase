package com.oyetech.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.oyetech.models.utils.states.ViewState
import com.oyetech.models.utils.states.ViewStatus

/**
Created by Erdi Özbek
-15.03.2022-
-16:32-
 **/

abstract class BaseListAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val FOOTER_VIEW_TYPE = 2
    val NORMAL_VIEW_TYPE = 31
    var viewState: ViewState<ViewStatus>? = null

    var currentList = arrayListOf<T>()

    open fun add(item: T) {
        currentList.add(item)
        notifyItemInserted(currentList.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun addList(list: List<T>?) {
        if (list.isNullOrEmpty()) {
            notifyDataSetChanged()
            return
        }

        for (result in list) {
            add(result)
        }
    }

    fun getItem(position: Int): T? {

        return currentList[position]
    }

    private fun hasExtraRow(): Boolean {

        return viewState != null && viewState!!.status != ViewStatus.SUCCESS
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            FOOTER_VIEW_TYPE
        } else {
            NORMAL_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {

        return currentList.size + if (hasExtraRow()) 1 else 0
    }

    /**
     * Set the current network state to the adapter
     * but this work only after the initial load
     * and the adapter already have list to add new loading raw to it
     * so the initial loading state the activity responsible for handle it
     *
     * @param newNetworkState the new network state
     */
    fun setNetworkState(newNetworkState: ViewState<ViewStatus>?) {
        if (currentList.size != 0) {
            val previousState = this.viewState
            val hadExtraRow = hasExtraRow()
            this.viewState = newNetworkState
            val hasExtraRow = hasExtraRow()
            if (hadExtraRow != hasExtraRow) {
                if (hadExtraRow) {
                    notifyItemRemoved(itemCount)
                } else {
                    notifyItemInserted(itemCount)
                }
            } else if (hasExtraRow && previousState !== newNetworkState) {
                notifyItemChanged(itemCount - 1)
            }
        }
    }
}
