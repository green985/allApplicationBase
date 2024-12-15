package com.oyetech.composebase.projectRadioFeature.screens.radioSearchList

import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import kotlinx.collections.immutable.ImmutableList

/**
Created by Erdi Özbek
-10.12.2024-
-15:59-
 **/

data class RadioSearchListUiState(
    val searchQuery: String,
    val searchResults: ImmutableList<RadioUIState>,
    val isLoading: Boolean,
    val expanded: Boolean,
    val error: String,
)

sealed class RadioSearchListEvent {
    data class SearchQueryChanged(val query: String) : RadioSearchListEvent()
    data class ExpandedChange(val expanded: Boolean) : RadioSearchListEvent()
}