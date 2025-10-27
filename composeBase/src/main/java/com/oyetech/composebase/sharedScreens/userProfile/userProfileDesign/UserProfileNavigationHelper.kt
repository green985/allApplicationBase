package com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign

import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes

/**
 * Navigation helper for User Profile screens
 *
 * Created by Warp Agent
 * -6.06.2025-
 * -22:28-
 */
object UserProfileNavigationHelper {

    /**
     * Navigate to User2Profile screen with receiverId parameter
     * @param receiverId User ID to view profile for. If empty, shows current user's profile
     * @return Navigation route string
     */
    fun buildUser2ProfileRoute(receiverId: String = ""): String {
        return QuestionAppProjectRoutes.User2Profile.route.replace(
            "{receiverId}",
            receiverId
        )
    }

    /**
     * Navigate to User2Profile screen for current user (no receiverId)
     */
    fun buildCurrentUserProfileRoute(): String {
        return buildUser2ProfileRoute("")
    }

    /**
     * Navigate to User2Profile screen for specific user
     */
    fun buildUserProfileRoute(userId: String): String {
        return buildUser2ProfileRoute(userId)
    }
}

