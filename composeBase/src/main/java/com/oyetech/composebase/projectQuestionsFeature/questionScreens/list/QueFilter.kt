package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.models.questionProject.questionOperation.QueTag

data class QueFilter(
    val adminFilterType: QuestionListAdminFilterType = QuestionListAdminFilterType.NONE,
    val selectedTagFilter: QueTag? = null,
) {
    fun hasActiveFilter(): Boolean {
        return adminFilterType != QuestionListAdminFilterType.NONE || selectedTagFilter != null
    }

    companion object {
        val DEFAULT = QueFilter()
    }
}
