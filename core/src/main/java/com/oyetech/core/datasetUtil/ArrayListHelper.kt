package com.oyetech.core.datasetUtil

/**
Created by Erdi Özbek
-28.05.2022-
-18:40-
 **/

object ArrayListHelper {

    fun <T> listToArrayList(list: List<T>?): ArrayList<T> {
        var arrayList = arrayListOf<T>()
        if (!list.isNullOrEmpty()) {
            arrayList.addAll(list)
        }

        return arrayList
    }
}
