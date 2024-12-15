package com.oyetech.models.radioProject.enums

import com.oyetech.models.radioProject.enums.ListSortType.A_Z
import com.oyetech.models.radioProject.enums.ListSortType.CLICK_COUNT

/**
Created by Erdi Özbek
-1.01.2023-
-17:46-
 **/

enum class ListSortType {
    CLICK_COUNT, A_Z
}

fun ListSortType.changeSortStatus(): ListSortType {
    var listType = this

    if (listType == CLICK_COUNT) {
        listType = A_Z
    } else {
        listType = CLICK_COUNT
    }

    return listType
}