package com.oyetech.composebase.projectQuestionsFeature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oyetech.composebase.projectQuestionsFeature.adminApprove.AdminApproveQuestionScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.homeScreen.QuestionsHomeScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion.QuestionCreateScreenSetup
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListScreenSetup
import com.oyetech.composebase.sharedScreens.navigation.ScreenKey
import com.oyetech.composebase.sharedScreens.settings.FacSettingsScreenSetup
import com.oyetech.composebase.sharedScreens.userList.UserListScreenSetup
import com.oyetech.composebase.sharedScreens.userProfile.UserProfileScreenSetup

@Suppress("LongMethod")
fun NavGraphBuilder.questionAppNavigation(navController: NavController) {
    composable(QuestionAppProjectRoutes.QuestionAppHomepage.route) {
        QuestionsHomeScreenSetup()
    }

    composable(QuestionAppProjectRoutes.QuestionAppSettings.route) {
        FacSettingsScreenSetup()
    }

    composable(QuestionAppProjectRoutes.MessageConversationList.route) {
        MessageConversationListScreenSetup()
    }

    composable(QuestionAppProjectRoutes.UserList.route) {
        UserListScreenSetup()
    }

    composable(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route) {
        QuestionCreateScreenSetup()
    }

    composable(QuestionAppProjectRoutes.AdminApproveQuestion.route) {
        AdminApproveQuestionScreenSetup()
    }

    // Complete Profile
    composable(QuestionAppProjectRoutes.CompleteProfileScreen.route) {
        com.oyetech.composebase.experimental.loginOperations.CompleteProfileScreenSetup()
    }

    // Message detail
    composable(
        route = "${QuestionAppProjectRoutes.MessageDetail.route}?" +
                "${ScreenKey.conversationId}={conversationId}" +
                "&${ScreenKey.receiverUserId}={receiverUserId}",
        arguments = listOf(
            navArgument(ScreenKey.conversationId) {
                defaultValue = ""
                nullable = true
            },
            navArgument(ScreenKey.receiverUserId) {
                defaultValue = ""
                nullable = false
            }
        )
    ) { entry ->
        val conversationId = entry.arguments?.getString(ScreenKey.conversationId) ?: ""
        val receiverUserId = entry.arguments?.getString(ScreenKey.receiverUserId) ?: ""
        com.oyetech.composebase.sharedScreens.messaging.MessageDetailScreenSetup(
            conversationId = conversationId,
            receiverUserId = receiverUserId
        )
    }

    // Newly added UserProfile route under Question app
    composable(
        route = "${QuestionAppProjectRoutes.UserProfile.route}?" +
                "&${ScreenKey.receiverUserId}={receiverUserId}",
        arguments = listOf(
            navArgument(ScreenKey.receiverUserId) {
                defaultValue = ""
                nullable = false
            }
        )
    ) { entry ->
        val receiverUserId = entry.arguments?.getString(ScreenKey.receiverUserId) ?: ""
        UserProfileScreenSetup(receiverUserId = receiverUserId)
    }
}
