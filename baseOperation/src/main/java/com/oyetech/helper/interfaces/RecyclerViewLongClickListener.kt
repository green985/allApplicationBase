package com.oyetech.helper.interfaces

import android.view.View

interface RecyclerViewLongClickListener<T> {
    fun itemOnLongClick(adapterPosition: Int, itemData: T? = null, view: View? = null): Boolean
}
