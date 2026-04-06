package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType

object QuestionListNavigationHelper {

    /**
     * Build a [AppRoute.QuestionListWithParams] with optional filters.
     */
    fun buildQuestionListWithParamsRoute(
        tagId: String? = null,
        adminFilterType: QuestionListAdminFilterType? = null,
    ): AppRoute.QuestionListWithParams = AppRoute.QuestionListWithParams(
        questionTag = tagId,
        adminFilterType = adminFilterType?.name
    )

    /** Build route filtered by a [QueTag]. */
    fun buildQuestionListWithTagRoute(tag: QueTag): AppRoute.QuestionListWithParams =
        buildQuestionListWithParamsRoute(tagId = tag.id)

    /** Build route filtered by an admin filter only. */
    fun buildQuestionListWithAdminFilterRoute(
        adminFilterType: QuestionListAdminFilterType,
    ): AppRoute.QuestionListWithParams =
        buildQuestionListWithParamsRoute(adminFilterType = adminFilterType)

    /** Build route filtered by both tag and admin filter. */
    fun buildQuestionListWithFiltersRoute(
        tag: QueTag,
        adminFilterType: QuestionListAdminFilterType,
    ): AppRoute.QuestionListWithParams =
        buildQuestionListWithParamsRoute(tagId = tag.id, adminFilterType = adminFilterType)
}
