package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.models.questionProject.questionOperation.QueTag

data class QueFilter(
    val adminFilterType: QuestionListAdminFilterType = QuestionListAdminFilterType.ALL,
    val selectedTagFilter: QueTag? = null,
) {
    fun hasActiveFilter(): Boolean {
        return adminFilterType != QuestionListAdminFilterType.ALL || selectedTagFilter != null
    }

    companion object {
        val DEFAULT = QueFilter()
    }
}
