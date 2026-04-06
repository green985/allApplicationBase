package com.oyetech.composebase.projectQuestionsFeature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oyetech.composebase.projectQuestionsFeature.adminApprove.AdminApproveQuestionScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.homeScreen.QuestionsHomeScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion.QuestionCreateScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListWithParamsScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionPagerScreenSetup
import com.oyetech.composebase.sharedScreens.navigation.ScreenKey
import com.oyetech.composebase.sharedScreens.settings.FacSettingsScreenSetup
import com.oyetech.composebase.sharedScreens.userList.UserListScreenSetup
import com.oyetech.composebase.sharedScreens.userProfile.editProfile.EditUserProfileScreenSetup
import com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign.User2ProfileScreenSetup

@Suppress("LongMethod")
fun NavGraphBuilder.questionAppNavigation(navController: NavController) {
    composable(QuestionAppProjectRoutes.QuestionAppHomepage.route) {
        QuestionsHomeScreenSetup()
    }

    composable(QuestionAppProjectRoutes.QuestionAppSettings.route) {
        FacSettingsScreenSetup()
    }

    composable(QuestionAppProjectRoutes.MessageConversationList.route) {
//        MessageConversationListScreenSetup()
    }

    composable(QuestionAppProjectRoutes.UserList.route) {
        UserListScreenSetup()
    }

    composable(
        route = "${QuestionAppProjectRoutes.QuestionCreateQuestionPage.route}?" +
                "${ScreenKey.questionId}={questionId}",
        arguments = listOf(
            navArgument(ScreenKey.questionId) {
                defaultValue = ""
                nullable = true
            }
        )
    ) { entry ->
        val questionId = entry.arguments?.getString(ScreenKey.questionId) ?: ""
        QuestionCreateScreenSetup(questionId = questionId)
    }

    composable(QuestionAppProjectRoutes.AdminApproveQuestion.route) {
        AdminApproveQuestionScreenSetup()
    }

    composable(QuestionAppProjectRoutes.QuestionPager.route) {
        QuestionPagerScreenSetup()
    }
    composable(QuestionAppProjectRoutes.EditProfile.route) {
        EditUserProfileScreenSetup()
    }

    composable(
        route = "${QuestionAppProjectRoutes.QuestionListWithParams.route}?" +
                "${ScreenKey.questionTag}={questionTag}" +
                "&${ScreenKey.adminFilterType}={adminFilterType}",
        arguments = listOf(
            navArgument(ScreenKey.questionTag) {
                defaultValue = ""
                nullable = true
            },
            navArgument(ScreenKey.adminFilterType) {
                defaultValue = ""
                nullable = true
            }
        )
    ) { entry ->
        val questionTag = entry.arguments?.getString(ScreenKey.questionTag)
        val adminFilterType = entry.arguments?.getString(ScreenKey.adminFilterType)
        QuestionListWithParamsScreenSetup(
            questionTagId = questionTag,
            adminFilterTypeStr = adminFilterType
        )
    }

    // Question Form Screen
    composable(
        route = "${QuestionAppProjectRoutes.QuestionFormScreen.route}?" +
                "${ScreenKey.formId}={formId}",
        arguments = listOf(
            navArgument(ScreenKey.formId) {
                defaultValue = ""
                nullable = true
            }
        )
    ) { entry ->
        val formId = entry.arguments?.getString(ScreenKey.formId) ?: ""
        com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.QuestionFormScreenSetup(
            formId = formId
        )
    }

    // Complete Profile
    composable(QuestionAppProjectRoutes.CompleteProfileScreen.route) {
        com.oyetech.composebase.experimental.loginOperations.CompleteProfileScreenSetup()
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
        User2ProfileScreenSetup(receiverUserId = receiverUserId)
    }
}
