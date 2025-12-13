package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestionFilterOperationHelper {
    private val _queFilter = MutableStateFlow(QueFilter.DEFAULT)
    val queFilter: StateFlow<QueFilter> = _queFilter.asStateFlow()

    fun setAdminFilter(filterType: QuestionListAdminFilterType) {
        _queFilter.value = _queFilter.value.copy(adminFilterType = filterType)
    }

    fun setTagFilter(tag: QueTag?) {
        _queFilter.value = _queFilter.value.copy(selectedTagFilter = tag)
    }

    fun clearAllFilters() {
        _queFilter.value = QueFilter.DEFAULT
    }

    fun getCurrentFilter(): QueFilter = _queFilter.value
}
