package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.models.questionProject.questionOperation.ModerationStatus
import com.oyetech.models.questionProject.questionOperation.ModerationStatus.APPROVED

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

fun QuestionListAdminFilterType.toModerationStatusOrNull(): ModerationStatus? {
    return when (this) {
        QuestionListAdminFilterType.APPROVED_ADMIN -> APPROVED
        QuestionListAdminFilterType.DECLINED_ADMIN -> ModerationStatus.DECLINED
        QuestionListAdminFilterType.PENDING_ADMIN -> ModerationStatus.PENDING
        QuestionListAdminFilterType.ALL -> ModerationStatus.ALL
    }
}