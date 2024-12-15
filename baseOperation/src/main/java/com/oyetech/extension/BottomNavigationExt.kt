package com.oyetech.extension

import androidx.core.view.forEachIndexed
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber

/**
Created by Erdi Özbek
-6.03.2022-
-18:07-
 **/

fun BottomNavigationView.getSelectedItemPosition(): Int {
    this.menu.forEachIndexed { index, item ->
        if (item.itemId == this.selectedItemId) {
            Timber.d("index == " + index)
            return index
        }
    }
    return 0
}

fun BottomNavigationView.getPositionFromSelectedId(selectionId: Int): Int {
    this.menu.forEachIndexed { index, item ->
        if (item.itemId == selectionId) {
            Timber.d("index == " + index)
            return index
        }
    }
    return 0
}

fun BottomNavigationView.setSelectedItemAndReturnId(tabIndex: Int): Int {
    this.menu.forEachIndexed { index, item ->
        if (tabIndex == index) {
            Timber.d("index == " + index)
            this.selectedItemId = item.itemId
            return this.selectedItemId
        }
    }
    return 0
}
