package com.oyetech.helper.interfaces

import android.view.View

/**
Created by Erdi Özbek
-15.03.2022-
-16:03-
 **/

interface RecyclerViewItemClickListener<T> {

    fun itemClickListener(adapterPosition: Int, itemData: T? = null, view: View? = null)
}
