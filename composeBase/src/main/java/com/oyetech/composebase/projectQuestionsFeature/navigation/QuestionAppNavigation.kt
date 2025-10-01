package com.oyetech.composebase.projectQuestionsFeature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.oyetech.composebase.projectQuestionsFeature.homeScreen.QuestionsHomeScreenSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.navigateRouteOperation
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListScreenSetup
import com.oyetech.composebase.sharedScreens.settings.FacSettingsScreenSetup
import com.oyetech.composebase.sharedScreens.userList.UserListScreenSetup

@Suppress("LongMethod")
fun NavGraphBuilder.questionAppNavigation(navController: NavController) {
    composable(QuestionAppProjectRoutes.QuestionAppHomepage.route) {
        QuestionsHomeScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuestionAppProjectRoutes.QuestionAppSettings.route) {
        FacSettingsScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuestionAppProjectRoutes.MessageConversationList.route) {
        MessageConversationListScreenSetup()
    }

    composable(QuestionAppProjectRoutes.UserList.route) {
        UserListScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }
}
