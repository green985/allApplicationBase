package com.oyetech.composebase.projectRadioFeature.screens.radioListScreen

import com.oyetech.composebase.projectRadioFeature.screens.radioPlayer.vm.mapToResponse
import com.oyetech.models.radioProject.enums.RadioListEnums.Country
import com.oyetech.models.radioProject.enums.RadioListEnums.Favorites
import com.oyetech.models.radioProject.enums.RadioListEnums.History
import com.oyetech.models.radioProject.enums.RadioListEnums.Languages
import com.oyetech.models.radioProject.enums.RadioListEnums.Last_Change
import com.oyetech.models.radioProject.enums.RadioListEnums.Last_Click
import com.oyetech.models.radioProject.enums.RadioListEnums.Local
import com.oyetech.models.radioProject.enums.RadioListEnums.Search
import com.oyetech.models.radioProject.enums.RadioListEnums.Tag
import com.oyetech.models.radioProject.enums.RadioListEnums.Top_Click
import com.oyetech.models.radioProject.enums.RadioListEnums.Top_Voted
import com.oyetech.tools.AppUtil
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map

/**
Created by Erdi Özbek
-12.12.2024-
-22:08-
 **/
@Suppress("CyclomaticComplexMethod", "LongMethod")
fun RadioListVM.setFlowableWithListType() {
    // will be fixed later...
    listHeaderString = radioListType
    when (radioListType) {
        Local.name -> {
            var countryString = AppUtil.getCountryCode(context)
            flowable =
                radioStationListOperationUseCase.getStationListWithCountryParams(countryString)
        }

        History.name -> {
            // will fixed.
            flowable = radioStationListOperationUseCase.getRadioListHistoryFlow()
        }

        Favorites.name -> {
            // will fixed.
            flowable = radioFavViewModelSlice.observeForList()
        }

        Last_Click.name -> {
            flowable = radioStationListOperationUseCase.getLastClickStationList()
        }

        Last_Change.name -> {
            flowable = radioStationListOperationUseCase.getLastChangeStationList()
        }

        Top_Voted.name -> {
            flowable = radioStationListOperationUseCase.getTopVotedStationList()
        }

        Top_Click.name -> {
            flowable = radioStationListOperationUseCase.getTopClickStationList()
        }

        Country.name -> {
            if (countryName.isNotBlank()) {
                flowable =
                    radioStationListOperationUseCase.getStationListWithCountryParams(
                        countryString = countryName
                    )
            }
        }

        Languages.name -> {
            if (languageName.isNotBlank()) {
                flowable =
                    radioStationListOperationUseCase.getStationListWithLanguageParams(
                        languageString = languageName
                    )
            }
        }

        Tag.name -> {
            if (tagName.isNotBlank()) {
                flowable =
                    radioStationListOperationUseCase.getStationListWithTagParams(tagString = tagName)
            }
        }

        Search.name -> {
            flowable = radioStationListOperationUseCase.getStationListWithSearchParams("")
        }

        else -> {
            flowable = radioStationListOperationUseCase.getTopClickStationList()
        }
    }

    flowable = flowable.map {
        if (it.isNotEmpty()) {
            radioDataOperationUseCase.saveLastRadioList(it)
            lastList = it.toImmutableList()
        }
        it
    }

    when (radioListType) {
        Favorites.name -> {
            radioUIStateFlow =
                flowable.mapToResponse(radioDataOperationUseCase, radioOperationUseCase)
                    .map { favList ->
                        val itemLists = if (complexItemViewState.value.isRefreshing) {
                            favList.toImmutableList()
                        } else {
                            var mergedList = complexItemViewState.value.items + favList
                            mergedList = mergedList.distinctBy { it.stationuuid }
                            mergedList
                        }
                        itemLists
                    }
        }

        else -> {
            radioUIStateFlow =
                flowable.mapToResponse(radioDataOperationUseCase, radioOperationUseCase)
        }
    }
}