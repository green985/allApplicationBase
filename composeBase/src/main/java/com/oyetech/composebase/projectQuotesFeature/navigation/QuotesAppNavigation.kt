package com.oyetech.composebase.projectQuotesFeature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oyetech.composebase.experimental.loginOperations.CompleteProfileScreenSetup
import com.oyetech.composebase.experimental.loginOperations.LoginOperationScreenSetup
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteScreenSetup
import com.oyetech.composebase.projectQuotesFeature.authorListScreen.AuthorListScreen
import com.oyetech.composebase.projectQuotesFeature.debug.adviceQuote.AdviceQuoteDebugScreenSetup
import com.oyetech.composebase.projectQuotesFeature.homeScreen.QuotesHomeScreenSetup
import com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen.QuoteSettingsScreenSetup
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailScreenSetup
import com.oyetech.composebase.projectQuotesFeature.searchScreen.QuoteSearchScreenSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.navigateRouteOperation
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe.ContactScreen
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailScreenSetup
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListScreenSetup
import com.oyetech.composebase.sharedScreens.settings.FacSettingsScreenSetup
import com.oyetech.composebase.sharedScreens.userList.UserListScreenSetup
import com.oyetech.composebase.sharedScreens.userProfile.UserProfileScreenSetup

@Suppress("LongMethod")
fun NavGraphBuilder.quotesAppNavigation(navController: NavController) {
    composable(QuoteAppProjectRoutes.QuoteAppHomepage.route) {
        QuotesHomeScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }
    composable(
        route = "${QuoteAppProjectRoutes.QuoteDetailRoute.route}?" +
                "${ScreenKey.quoteId}={quoteId}", arguments = listOf(
            navArgument(ScreenKey.quoteId) {
                defaultValue = "randomSingle"
                nullable = false
            },
        )
    ) {
        val quoteId = it.arguments?.getString(ScreenKey.quoteId) ?: "randomSingle"
        QuoteDetailScreenSetup(
            quoteId = quoteId,
            navigationRoute = navigateRouteOperation(navController)
        )
    }
    composable(QuoteAppProjectRoutes.QuoteAuthorList.route) {
        AuthorListScreen(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuoteAppProjectRoutes.QuoteAdviceScreen.route) {
        AdviceQuoteScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuoteAppProjectRoutes.QuoteSettings.route) {
        QuoteSettingsScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuoteAppProjectRoutes.AdviceQuoteDebug.route) {
        AdviceQuoteDebugScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuoteAppProjectRoutes.ContactScreen.route) {
        ContactScreen(navigationRoute = navigateRouteOperation(navController))
    }

    // TabHistory Route
    composable(QuoteAppProjectRoutes.LoginOperationScreen.route) {
        LoginOperationScreenSetup(
            navigationRoute = navigateRouteOperation(navController)
        )
    }
    // TabHistory Route
    composable(QuoteAppProjectRoutes.SearchScreen.route) {
        QuoteSearchScreenSetup(
            navigationRoute = navigateRouteOperation(navController)
        )
    }
    // TabHistory Route
    composable(QuoteAppProjectRoutes.CompleteProfileScreen.route) {
        CompleteProfileScreenSetup(
            navigationRoute = navigateRouteOperation(navController)
        )
    }

    composable(QuoteAppProjectRoutes.MessageConversationList.route) {
        MessageConversationListScreenSetup(
            navigationRoute = navigateRouteOperation(navController)
        )
    }


    composable(
        route = "${QuoteAppProjectRoutes.MessageDetail.route}?" +
                "${ScreenKey.conversationId}={conversationId}" +
                "&${ScreenKey.receiverUserId}={receiverUserId}", arguments = listOf(
            navArgument(ScreenKey.conversationId) {
                defaultValue = ""
                nullable = true
            },
            navArgument(ScreenKey.receiverUserId) {
                defaultValue = ""
                nullable = false
            },
        )
    ) {
        val conversationId = it.arguments?.getString(ScreenKey.conversationId) ?: ""
        val receiverUserId = it.arguments?.getString(ScreenKey.receiverUserId) ?: ""
        MessageDetailScreenSetup(
            conversationId = conversationId,
            receiverUserId = receiverUserId,
            navigationRoute = navigateRouteOperation(navController)
        )
    }
    composable(
        route = "${QuoteAppProjectRoutes.UserProfile.route}?" +
                "&${ScreenKey.receiverUserId}={receiverUserId}", arguments = listOf(
            navArgument(ScreenKey.receiverUserId) {
                defaultValue = ""
                nullable = false
            },
        )
    ) {
        val receiverUserId = it.arguments?.getString(ScreenKey.receiverUserId) ?: ""
        UserProfileScreenSetup(
            receiverUserId = receiverUserId,
            navigationRoute = navigateRouteOperation(navController)
        )
    }


    composable(QuoteAppProjectRoutes.FacSettings.route) {
        FacSettingsScreenSetup(
            navigationRoute = navigateRouteOperation(navController)
        )
    }

    composable(QuoteAppProjectRoutes.UserList.route) {
        UserListScreenSetup(
            navigationRoute = navigateRouteOperation(navController)
        )
    }

}