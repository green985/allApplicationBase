package com.oyetech.composebase.projectRadioFeature.screens.tabAllList

import com.oyetech.models.radioProject.enums.RadioListEnums
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
Created by Erdi Özbek
-18.11.2024-
-20:45-
 **/

data class RadioAllListUiState(
    val tabNameList: ImmutableList<String> = emptyList<String>().toImmutableList(),
    val tabEnumList: ImmutableList<RadioListEnums> = emptyList<RadioListEnums>().toImmutableList(),
    val selectedIndex: Int = 0,
)