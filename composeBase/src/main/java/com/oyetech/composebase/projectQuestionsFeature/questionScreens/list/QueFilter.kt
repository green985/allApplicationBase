package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.models.questionProject.questionOperation.QueTag

data class QueFilter(
    val adminFilterType: QuestionListAdminFilterType = QuestionListAdminFilterType.APPROVED_ADMIN,
    val selectedTagFilter: QueTag? = null,
    val questionListType: String? = null,
    val userId: String? = null,
) {
    fun hasActiveFilter(): Boolean {
        return adminFilterType != QuestionListAdminFilterType.APPROVED_ADMIN || selectedTagFilter != null
    }

    companion object {
        val DEFAULT = QueFilter()
    }
}

