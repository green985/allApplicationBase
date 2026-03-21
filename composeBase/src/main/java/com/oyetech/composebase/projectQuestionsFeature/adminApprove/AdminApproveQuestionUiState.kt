package com.oyetech.composebase.projectQuestionsFeature.adminApprove

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// UI state for Admin Approve Questions
data class AdminApproveQuestionUiState(
    val errorText: String = "",
    val isLoading: Boolean = false,
    val pendingCountText: String = "0 pending",
    val currentFilter: QueFilter = QueFilter.DEFAULT,
    val currentPage: Int = 0,

    val currentFilterType: QuestionListAdminFilterType = QuestionListAdminFilterType.ALL,
    val tabs: ImmutableList<Pair<QuestionListAdminFilterType, String>> = persistentListOf(
        QuestionListAdminFilterType.ALL to LanguageKey.all,
        QuestionListAdminFilterType.APPROVED_ADMIN to LanguageKey.approved,
        QuestionListAdminFilterType.DECLINED_ADMIN to LanguageKey.declined,
        QuestionListAdminFilterType.PENDING_ADMIN to LanguageKey.pending,
    ),
)

// UI events
sealed class AdminApproveQuestionUiEvent : BaseUIEvent() {
    data object OnIdle : AdminApproveQuestionUiEvent()
}

// View events
sealed class AdminApproveQuestionEvent : BaseEvent() {
    data class OnFilterSelected(val filterType: QuestionListAdminFilterType) :
        AdminApproveQuestionEvent()

    data class OnTagFilterChanged(
        val tag: QueTag? = null,
        val adminFilterType: QuestionListAdminFilterType? = null,
    ) :
        AdminApproveQuestionEvent()

    data object OnRefreshClicked : AdminApproveQuestionEvent()
}
