package com.oyetech.composebase.projectRadioFeature.helper

import com.oyetech.composebase.base.baseGenericList.ItemSortType.DefaultSortType
import com.oyetech.composebase.base.baseGenericList.ItemSortType.NameSortType
import com.oyetech.domain.repository.helpers.logicRepositories.RadioListSortRepository
import com.oyetech.models.radioProject.entity.radioEntity.language.LanguageResponseData
import com.oyetech.models.radioProject.entity.radioEntity.station.RadioStationResponseData
import kotlinx.collections.immutable.toImmutableList

/**
Created by Erdi Özbek
-5.12.2024-
-20:47-
 **/

@Suppress("UNCHECKED_CAST")
class RadioListSortRepositoryImp : RadioListSortRepository {

    lateinit var lastRadioList: List<String>
    lateinit var lastLanguageList: List<String>

    override fun sortList(list: List<Any>, sortType: String): List<*> {
        when (list.firstOrNull()) {
            is RadioStationResponseData -> {
                return radioListSort(
                    sortType,
                    list as List<RadioStationResponseData>
                )
            }

            is LanguageResponseData -> {

                return languageListSort(
                    sortType,
                    list as List<LanguageResponseData>
                )
            }
        }
        return list
    }

    private fun languageListSort(
        sortType: String,
        list: List<LanguageResponseData>,
    ): List<LanguageResponseData> {
        if (::lastLanguageList.isInitialized.not()) {
            lastLanguageList = list.map {
                it.languageName
            }
        }
        val sortedList = when (sortType) {
            DefaultSortType.toString() -> {
                val newList = ArrayList<LanguageResponseData>()
                lastLanguageList.forEach { defaultListItem ->
                    val item = list.find {
                        it.languageName == defaultListItem
                    }
                    if (item != null) {
                        newList.add(item)
                    }
                }
                newList.toImmutableList()
            }

            NameSortType.toString() -> {
                list.sortedBy {
                    it.languageName
                }.toImmutableList()
            }

            else -> {
                list
            }
        }
        return sortedList
    }

    private fun radioListSort(
        sortType: String,
        list: List<RadioStationResponseData>,
    ): List<RadioStationResponseData> {
        if (::lastRadioList.isInitialized.not()) {
            lastRadioList = list.map {
                it.stationuuid
            }
        }
        val sortedList = when (sortType) {
            DefaultSortType.toString() -> {
                val newList = ArrayList<RadioStationResponseData>()
                lastRadioList.forEach { defaultListItem ->
                    val item = list.find {
                        it.stationuuid == defaultListItem
                    }
                    if (item != null) {
                        newList.add(item)
                    }
                }
                newList.toImmutableList()
            }

            NameSortType.toString() -> {
                list.sortedBy {
                    it.radioName
                }.toImmutableList()
            }

            else -> {
                list
            }
        }
        return sortedList
    }
}