package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

enum class QuestionListAdminFilterType {
    ALL,
    APPROVED_ADMIN,
    DECLINED_ADMIN,
    PENDING_ADMIN,
}

object QuestionListAdminFilterTypeCatalog {
    val filtersWithNameTag = listOf(
        QuestionListAdminFilterType.ALL to "All",
        QuestionListAdminFilterType.PENDING_ADMIN to "Pending",
        QuestionListAdminFilterType.APPROVED_ADMIN to "Approved",
        QuestionListAdminFilterType.DECLINED_ADMIN to "Declined",
    )

    val allAdminFilters = listOf(
        QuestionListAdminFilterType.ALL,
        QuestionListAdminFilterType.APPROVED_ADMIN,
        QuestionListAdminFilterType.DECLINED_ADMIN,
        QuestionListAdminFilterType.PENDING_ADMIN,
    )
}