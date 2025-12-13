package com.oyetech.composebase.projectQuestionsFeature.questionScreens.list

import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.sharedScreens.navigation.ScreenKey
import com.oyetech.models.questionProject.questionOperation.QueTag
import com.oyetech.models.questionProject.questionOperation.QuestionListAdminFilterType

object QuestionListNavigationHelper {

    /**
     * Navigate to QuestionListWithParams with optional filters
     * @param tagId Tag id to filter by (optional)
     * @param adminFilterType Admin filter type to filter by (optional)
     * @return Navigation route string
     */
    fun buildQuestionListWithParamsRoute(
        tagId: String? = null,
        adminFilterType: QuestionListAdminFilterType? = null,
    ): String {
        val baseRoute = QuestionAppProjectRoutes.QuestionListWithParams.route
        val params = mutableListOf<String>()

        tagId?.let {
            params.add("${ScreenKey.questionTag}=$it")
        }

        adminFilterType?.let {
            params.add("${ScreenKey.adminFilterType}=${it.name}")
        }

        return if (params.isEmpty()) {
            baseRoute
        } else {
            "$baseRoute?${params.joinToString("&")}"
        }
    }

    /**
     * Navigate to QuestionListWithParams with QueTag object
     */
    fun buildQuestionListWithTagRoute(tag: QueTag): String {
        return buildQuestionListWithParamsRoute(tagId = tag.id)
    }

    /**
     * Navigate to QuestionListWithParams with admin filter only
     */
    fun buildQuestionListWithAdminFilterRoute(
        adminFilterType: QuestionListAdminFilterType,
    ): String {
        return buildQuestionListWithParamsRoute(adminFilterType = adminFilterType)
    }

    /**
     * Navigate to QuestionListWithParams with both tag and admin filter
     */
    fun buildQuestionListWithFiltersRoute(
        tag: QueTag,
        adminFilterType: QuestionListAdminFilterType,
    ): String {
        return buildQuestionListWithParamsRoute(
            tagId = tag.id,
            adminFilterType = adminFilterType
        )
    }
}
