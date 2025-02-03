package com.oyetech.composebase.projectQuotesFeature.quotes.tagList

import androidx.compose.runtime.mutableStateOf
import com.oyetech.composebase.R
import com.oyetech.composebase.base.baseList.BaseListViewModel
import com.oyetech.composebase.base.baseList.ComplexItemListState
import com.oyetech.composebase.projectRadioFeature.screens.views.toolbar.RadioToolbarState
import com.oyetech.models.quotes.responseModel.QuotesTagResponseData
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow

/**
Created by Erdi Özbek
-19.12.2024-
-22:35-
 **/

class QuoteTagListVM(appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers) :
    BaseListViewModel<QuoteTagUiState>(appDispatchers) {

    val toolbarState = mutableStateOf(
        RadioToolbarState(
            title = context.getString(R.string.action_tags),
        )
    )

    override val complexItemViewState:
            MutableStateFlow<ComplexItemListState<QuoteTagUiState>> = MutableStateFlow(
        ComplexItemListState(errorMessage = errorMessage)
    )

    init {
        populateTagList()
    }

    private fun populateTagList() {
        val tagList = QuotesTagResponseData.getTopicsList().map {
            QuoteTagUiState(
                tagName = it,
            )
        }.toImmutableList()
        complexItemViewState.value = ComplexItemListState(items = tagList)
    }

}