package com.oyetech.composebase.navigator

import androidx.annotation.Keep
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Single source of truth for all navigation destinations.
 * Every route is a @Serializable typed object/class nested inside [AppRoute].
 * Reference them as AppRoute.QuestionAppHomepage, AppRoute.QuestionCreateQuestionPage(), etc.
 */
@Keep
sealed interface AppRoute : NavKey {

    // ── Outer-shell routes (debug launcher / AllScreenNavigator) ─────────────

    @Keep
    @Serializable
    data object AppFullApp : AppRoute

    @Keep
    @Serializable
    data object QuestionAppStart : AppRoute

    // ── Question-app destinations ─────────────────────────────────────────────

    @Keep
    @Serializable
    data object QuestionAppHomepage : AppRoute

    @Keep
    @Serializable
    data object QuestionAppSettings : AppRoute

    @Keep
    @Serializable
    data object AdminApproveQuestion : AppRoute

    @Keep
    @Serializable
    data object QuestionPager : AppRoute

    @Keep
    @Serializable
    data object CompleteProfileScreen : AppRoute

    @Keep
    @Serializable
    data object EditProfile : AppRoute

    @Keep
    @Serializable
    data object UserList : AppRoute

    @Keep
    @Serializable
    data object MessageConversationList : AppRoute

    // ── Parameterised destinations ────────────────────────────────────────────

    @Keep
    @Serializable
    data class QuestionCreateQuestionPage(val questionId: String = "") : AppRoute

    @Keep
    @Serializable
    data class QuestionListWithParams(
        val questionTag: String? = null,
        val adminFilterType: String? = null,
    ) : AppRoute

    @Keep
    @Serializable
    data class UserProfile(val receiverUserId: String = "") : AppRoute

    @Keep
    @Serializable
    data class QuestionFormScreen(val formId: String = "") : AppRoute

    @Keep
    @Serializable
    data class MessageDetail(
        val receiverUserId: String = "",
        val conversationId: String = "",
    ) : AppRoute
}
