package com.oyetech.domain.repository.helpers.logicRepositories

/**
Created by Erdi Özbek
-5.12.2024-
-20:41-
 **/

interface RadioListSortRepository {
    fun sortList(list: List<Any>, sortType: String): List<*>
}