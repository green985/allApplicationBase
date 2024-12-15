package com.oyetech.helper.interfaces

import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener

/**
Created by Erdi Özbek
-6.12.2023-
-23:43-
 **/
interface MenuItemClickListenerWithData<T> : OnMenuItemClickListener {

    fun onMenuItemClickWithData(item: MenuItem?, data: T): Boolean
}
